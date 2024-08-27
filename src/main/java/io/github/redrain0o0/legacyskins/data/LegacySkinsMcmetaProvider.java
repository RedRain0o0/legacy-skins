package io.github.redrain0o0.legacyskins.data;

import com.google.common.hash.Hashing;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraft.SharedConstants;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class LegacySkinsMcmetaProvider implements DataProvider {

	private final PackOutput packOutput;

	public LegacySkinsMcmetaProvider(PackOutput packOutput) {
		this.packOutput = packOutput;
	}
	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	@Override
	public CompletableFuture<?> run(CachedOutput arg) {
		return CompletableFuture.runAsync(() -> {
			Mcmeta mcmeta = new Mcmeta(new Pack(SharedConstants.RESOURCE_PACK_FORMAT, new SupportedFormats(0, 2147438647), "Mod resources for Legacy Skins."));
			JsonElement json = Mcmeta.CODEC.encodeStart(JsonOps.INSTANCE, mcmeta).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
			byte[] bytes = gson.toJson(json).getBytes(StandardCharsets.UTF_8);
			try {
				arg.writeIfNeeded(packOutput.getOutputFolder().resolve("pack.mcmeta"), bytes, Hashing.sha1().hashBytes(bytes));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public String getName() {
		return "pack.mcmeta";
	}

	private record Mcmeta(Pack pack) {
		public static final Codec<Mcmeta> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Pack.CODEC.fieldOf("pack").forGetter(Mcmeta::pack)
		).apply(instance, Mcmeta::new));
	}

	private record Pack(int packFormat, SupportedFormats supportedFormats, String description) {
		public static final Codec<Pack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("pack_format").forGetter(Pack::packFormat),
				SupportedFormats.CODEC.fieldOf("supported_formats").forGetter(Pack::supportedFormats),
				Codec.STRING.fieldOf("description").forGetter(Pack::description)
		).apply(instance, Pack::new));
	}

	private record SupportedFormats(int minInclusive, int maxInclusive) {
		public static final Codec<SupportedFormats> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("min_inclusive").forGetter(SupportedFormats::minInclusive),
				Codec.INT.fieldOf("max_inclusive").forGetter(SupportedFormats::maxInclusive)
		).apply(instance, SupportedFormats::new));
	}
}
