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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class LegacyPackProvider implements DataProvider {

	private final FabricDataOutput dataOutput;
	private final Map<ResourceLocation, LegacySkinPack> packs = new LinkedHashMap<>();

	public LegacyPackProvider(FabricDataOutput dataOutput) {
		this.dataOutput = dataOutput;
	}

	@Override
	public CompletableFuture<?> run(CachedOutput cachedOutput) {
		return CompletableFuture.supplyAsync(() -> {
			addPacks();
			JsonElement packs = LegacySkinPack.MAP_CODEC.encodeStart(JsonOps.INSTANCE, this.packs).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
			return DataProvider.saveStable(cachedOutput, packs, dataOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(dataOutput.getModId()).resolve("skin_packs.json"));
		});
	}

	public void addPack(ResourceLocation id, LegacySkinPack pack) {
		this.packs.put(id, pack);
	}

	public void addPack(String id, LegacySkinPack pack) {
		this.addPack(!id.contains(":") ? ResourceLocation.fromNamespaceAndPath(dataOutput.getModId(), id) : ResourceLocation.parse(id), pack);
	}

	public abstract void addPacks();

	@Override
	public String getName() {
		return "Legacy Packs: %s".formatted(dataOutput.getModId());
	}
}
