package io.github.redrain0o0.legacyskins.client;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import wily.legacy.Legacy4J;
import wily.legacy.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public record LegacySkinPack(ResourceLocation icon, List<LegacySkin> skins) {
    public static final Map<String, LegacySkinPack> list = new LinkedHashMap<>();
    private static final String PACKS = "skin_packs.json";
	public static final Codec<LegacySkinPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("icon").forGetter(LegacySkinPack::icon),
			Codec.list(LegacySkin.CODEC).fieldOf("skins").forGetter(LegacySkinPack::skins)
	).apply(instance, LegacySkinPack::new));

    public static class Manager implements SimpleResourceReloadListener<Map<String, LegacySkinPack>> {
		@Override
		public CompletableFuture<Map<String, LegacySkinPack>> load(ResourceManager resourceManager, ProfilerFiller profiler, Executor executor) {
			return CompletableFuture.supplyAsync(() -> {
				Map<String, LegacySkinPack> packs = new LinkedHashMap<>();
				JsonUtil.getOrderedNamespaces(resourceManager).forEach(name->{
					resourceManager.getResource(ResourceLocation.tryBuild(name, PACKS)).ifPresent(r->{
						try {
							BufferedReader bufferedReader = r.openAsReader();
							JsonObject obj = GsonHelper.parse(bufferedReader);
							obj.asMap().forEach((s,element)->{
								// yikes!
								if (element instanceof JsonObject tabObj) {
									//packs.add(new LegacySkinPack(Component.translatable(s),ResourceLocation.parse(GsonHelper.getAsString(tabObj,"icon")),ResourceLocation.parse(GsonHelper.getAsString(tabObj,"skins"))));
									LegacySkinPack deserialized = CODEC.parse(JsonOps.INSTANCE, tabObj).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
									packs.put(s, deserialized);
								}
							});
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
		public CompletableFuture<Void> apply(Map<String, LegacySkinPack> data, ResourceManager manager, ProfilerFiller profiler, Executor executor) {
			list.clear();
			list.putAll(data);
			return CompletableFuture.completedFuture(null);
		}

		@Override
		public ResourceLocation getFabricId() {
			return ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "manager");
		}
	}
}
