package io.github.redrain0o0.legacyskins.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

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
		TreeMap<ResourceLocation, LegacySkinPack> packs = new TreeMap<>();
		return registryLookup.thenCompose(v -> {
			addPacks(new InternalPackBuilder(packs));
			JsonElement element = LegacySkinPack.MAP_CODEC.encodeStart(JsonOps.INSTANCE, packs).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
			System.out.println(element);
			return DataProvider.saveStable(cachedOutput, element, dataOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(dataOutput.getModId()).resolve("skin_packs.json"));
		});
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
		private final TreeMap<ResourceLocation, LegacySkinPack> packs;

		public InternalPackBuilder(TreeMap<ResourceLocation, LegacySkinPack> packs) {
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
