package io.github.redrain0o0.legacyskins.data;

import com.google.common.hash.Hashing;
import com.google.common.hash.HashingOutputStream;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.migrator.Migrator;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
//? if fabric
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
import java.util.concurrent.CompletableFuture;

public abstract class LegacyPackProvider implements DataProvider {

	private final PackOutput dataOutput;
	private final CompletableFuture<HolderLookup.Provider> registryLookup;
	//? if !fabric
	/*private String modid;*/

	//? if !fabric {
	/*public LegacyPackProvider(PackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		this(dataOutput, registryLookup, "minecraft");
	}
	*///?}

	public LegacyPackProvider(PackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup /*? if !fabric {*//*, String modid *//*?}*/) {
		this.dataOutput = dataOutput;
		this.registryLookup = registryLookup;
		//? if !fabric
		/*this.modid = modid;*/
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		LinkedHashMap<ResourceLocation, LegacySkinPack> packs = new LinkedHashMap<>();
		return registryLookup.thenCompose(v -> {
			InternalPackBuilder internalPackBuilder = new InternalPackBuilder(packs);
			addPacks(internalPackBuilder);
			JsonElement element = LegacySkinPack.MAP_CODEC.encodeStart(JsonOps.INSTANCE, packs).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
			element = Migrator.SKIN_PACKS_FIXER.addSchemaVersion(JsonOps.INSTANCE, element);
			return saveNotStable(cachedOutput, element, dataOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(internalPackBuilder.getModId()).resolve("skin_packs.json"));
		});
	}

	static CompletableFuture<?> saveNotStable(CachedOutput cachedOutput, JsonElement jsonElement, Path path) {
		return CompletableFuture.runAsync(() -> {
			try {
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				// noinspection UnstableApiUsage
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
		return "Legacy Packs";
	}

	public sealed interface PackBuilder {
		void addPack(ResourceLocation id, LegacySkinPack pack);

		default void addPack(String id, LegacySkinPack pack) {
			this.addPack(id(id), pack);
		}

		default ResourceLocation id(String id) {
			return !id.contains(":") ? VersionUtils.of(getModId(), id) : VersionUtils.parse(id);
		}

		PackOutput getOutput();

		String getModId();
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
		public PackOutput getOutput() {
			return dataOutput;
		}

		@Override
		public String getModId() {
			//? if fabric
			return ((FabricDataOutput) getOutput()).getModId();
			//? if !fabric
			/*return modid;*/
		}
	}
}
