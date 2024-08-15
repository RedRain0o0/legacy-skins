package io.github.redrain0o0.legacyskins.client;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import wily.legacy.Legacy4J;
import wily.legacy.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record LegacySkinPack(ResourceLocation icon, List<LegacySkin> skins) {
	public static final Map<ResourceLocation, LegacySkinPack> list = new LinkedHashMap<>();
	public static final Codec<LegacySkinPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("icon").forGetter(LegacySkinPack::icon),
			Codec.list(LegacySkin.CODEC).fieldOf("skins").forGetter(LegacySkinPack::skins)
	).apply(instance, LegacySkinPack::new));
	public static final Codec<Map<ResourceLocation, LegacySkinPack>> MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, LegacySkinPack.CODEC);
	private static final String PACKS = "skin_packs.json";

	public static class Manager implements SimpleResourceReloadListener<Map<ResourceLocation, LegacySkinPack>> {
		@Override
		public CompletableFuture<Map<ResourceLocation, LegacySkinPack>> load(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
			return CompletableFuture.supplyAsync(() -> {
				Map<ResourceLocation, LegacySkinPack> packs = new LinkedHashMap<>();
				JsonUtil.getOrderedNamespaces(resourceManager).forEach(name -> {
					resourceManager.getResource(ResourceLocation.tryBuild(name, PACKS)).ifPresent(r -> {
						try {
							BufferedReader bufferedReader = r.openAsReader();
							JsonObject obj = GsonHelper.parse(bufferedReader);
							Map<ResourceLocation, LegacySkinPack> map = MAP_CODEC.parse(JsonOps.INSTANCE, obj).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
							packs.putAll(map);
							bufferedReader.close();
						} catch (IOException var8) {
							Legacy4J.LOGGER.warn(var8.getMessage());
						}
					});
				});
				return packs;
			});
		}

		@Override
		public CompletableFuture<Void> apply(Map<ResourceLocation, LegacySkinPack> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
			LegacySkinUtils.cleanup();
			list.clear();
			list.putAll(data);
			LegacySkinUtils.switchSkin(Legacyskins.INSTANCE.skin.orElse(null));
			return CompletableFuture.completedFuture(null);
		}

		@Override
		public ResourceLocation getFabricId() {
			return ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "manager");
		}
	}
}
