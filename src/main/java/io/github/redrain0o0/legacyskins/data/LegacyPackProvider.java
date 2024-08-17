package io.github.redrain0o0.legacyskins.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

public abstract class LegacyPackProvider implements DataProvider {

	private final FabricDataOutput dataOutput;
	private final CompletableFuture<HolderLookup.Provider> registryLookup;

	public LegacyPackProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		this.dataOutput = dataOutput;
		this.registryLookup = registryLookup;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		LinkedHashMap<ResourceLocation, LegacySkinPack> packs = new LinkedHashMap<>();
		return registryLookup.thenCompose(v -> {
			addPacks(new InternalPackBuilder(packs));
			JsonElement element = LegacySkinPack.MAP_CODEC.encodeStart(JsonOps.INSTANCE, packs).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
			System.out.println(element);
			return saveNotStable(cachedOutput, element, dataOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(dataOutput.getModId()).resolve("skin_packs.json"));
		});
	}

	static CompletableFuture<?> saveNotStable(CachedOutput cachedOutput, JsonElement jsonElement, Path path) {
		return CompletableFuture.runAsync(() -> {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				HashingOutputStream hashingOutputStream = new HashingOutputStream(Hashing.sha1(), byteArrayOutputStream);
				JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(hashingOutputStream, StandardCharsets.UTF_8));

				try {
					jsonWriter.setSerializeNulls(false);
					jsonWriter.setIndent("  ");
					GsonHelper.writeValue(jsonWriter, jsonElement, null);
				} catch (Throwable var9) {
					try {
						jsonWriter.close();
					} catch (Throwable var8) {
						var9.addSuppressed(var8);
					}

					throw var9;
				}

				jsonWriter.close();
				cachedOutput.writeIfNeeded(path, byteArrayOutputStream.toByteArray(), hashingOutputStream.hash());
			} catch (IOException var10) {
				LOGGER.error("Failed to save file to {}", path, var10);
			}
		}, Util.backgroundExecutor());
	}

	public abstract void addPacks(PackBuilder builder);

	@Override
	public String getName() {
		return "Legacy Packs: %s".formatted(dataOutput.getModId());
	}

	public sealed interface PackBuilder {
		void addPack(ResourceLocation id, LegacySkinPack pack);

		default void addPack(String id, LegacySkinPack pack) {
			this.addPack(!id.contains(":") ? ResourceLocation.fromNamespaceAndPath(getOutput().getModId(), id) : ResourceLocation.parse(id), pack);
		}

		FabricDataOutput getOutput();
	}

	private final class InternalPackBuilder implements PackBuilder {
		private final LinkedHashMap<ResourceLocation, LegacySkinPack> packs;

		public InternalPackBuilder(LinkedHashMap<ResourceLocation, LegacySkinPack> packs) {
			this.packs = packs;
		}

		@Override
		public void addPack(ResourceLocation id, LegacySkinPack pack) {
			packs.put(id, pack);
		}

		@Override
		public FabricDataOutput getOutput() {
			return dataOutput;
		}
	}
}
