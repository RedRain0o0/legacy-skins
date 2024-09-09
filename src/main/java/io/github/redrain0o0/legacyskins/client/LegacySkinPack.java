package io.github.redrain0o0.legacyskins.client;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import io.github.redrain0o0.legacyskins.migrator.Migrator;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import wily.legacy.Legacy4J;
import wily.legacy.util.JsonUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
//? if fabric {
/*import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
*///?} else if neoforge || forge {
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
//?}

public record LegacySkinPack(LegacyPackType type, ResourceLocation icon, List<LegacySkin> skins) {
	public static final Map<ResourceLocation, LegacySkinPack> list = new LinkedHashMap<>();
	public static final Codec<LegacySkinPack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			LegacyPackType.CODEC.optionalFieldOf("type", LegacyPackType.DEFAULT).forGetter(LegacySkinPack::type),
			ResourceLocation.CODEC.fieldOf("icon").forGetter(LegacySkinPack::icon),
			Codec.list(LegacySkin.CODEC).fieldOf("skins").xmap(a -> (List<LegacySkin>) new ArrayList<>(a), a -> a).forGetter(LegacySkinPack::skins)
	).apply(instance, LegacySkinPack::new));
	public static final Codec<Map<ResourceLocation, LegacySkinPack>> MAP_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, LegacySkinPack.CODEC);
	private static final String PACKS = "skin_packs.json";

	//? if fabric
	/*public static class Manager implements SimpleResourceReloadListener<Map<ResourceLocation, LegacySkinPack>> {*/
	//? if neoforge || forge
	public static class Manager extends SimplePreparableReloadListener<Map<ResourceLocation, LegacySkinPack>> {
		@Override
		public /*? if fabric {*/ /*CompletableFuture< *//*?}*/ Map<ResourceLocation, LegacySkinPack> /*? if fabric {*/ /*> load *//*?} else if neoforge || forge {*/ prepare /*?}*/(ResourceManager resourceManager, ProfilerFiller profiler /*? if fabric {*//*, Executor executor *//*?}*/) {
			//? if fabric {
			/*return CompletableFuture.supplyAsync(() -> {
				return loadPacksFromResourceManager(resourceManager);
			});
			*///?} else if neoforge || forge {
			return loadPacksFromResourceManager(resourceManager);
			//?}
		}

		private static @NotNull Map<ResourceLocation, LegacySkinPack> loadPacksFromResourceManager(ResourceManager resourceManager) {
			Map<ResourceLocation, LegacySkinPack> packs = new LinkedHashMap<>();
			List<String> allNamespaces = JsonUtil.getOrderedNamespaces(resourceManager).toList();
			allNamespaces.stream().filter(Legacyskins.MOD_ID::equals).forEach(loadPackData(resourceManager, packs));
			allNamespaces.stream().filter(a -> !Legacyskins.MOD_ID.equals(a)).forEach(loadPackData(resourceManager, packs));
			return packs;
		}

		private static @NotNull Consumer<String> loadPackData(ResourceManager resourceManager, Map<ResourceLocation, LegacySkinPack> packs) {
			return name -> {
				resourceManager.getResource(ResourceLocation.tryBuild(name, PACKS)).ifPresent(r -> {
					try {
						BufferedReader bufferedReader = r.openAsReader();
						JsonElement obj = GsonHelper.parse(bufferedReader);
						obj = Migrator.SKIN_PACKS_FIXER.fix(JsonOps.INSTANCE, obj);
						Map<ResourceLocation, LegacySkinPack> map = MAP_CODEC.parse(JsonOps.INSTANCE, obj).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
						packs.putAll(map);
						bufferedReader.close();
					} catch (IOException var8) {
						Legacy4J.LOGGER.warn(var8.getMessage());
					}
				});
			};
		}

		// addFirst does not exist before Java 21
		@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
		@Override
		public /*? if fabric {*/ /*CompletableFuture<Void> *//*?} else if neoforge || forge {*/ void /*?}*/ apply(Map<ResourceLocation, LegacySkinPack> data, ResourceManager manager, ProfilerFiller profiler /*? if fabric {*/ /*, Executor executor *//*?}*/) {
			LegacySkinUtils.cleanup();
			list.clear();
			// The default skin
			data.get(Constants.DEFAULT_PACK).skins().add(0, null);
			list.putAll(data);
			Optional<SkinReference> skin = Legacyskins.lazyInstance().skin;
			if (skin.isPresent()) {
				SkinReference skinReference = skin.get();
				try {
					LegacySkin legacySkin = list.get(skinReference.pack()).skins().get(skinReference.ordinal());
					LegacySkinUtils.switchSkin(legacySkin);
				} catch (Throwable t) {
					Legacyskins.LOGGER.error("Failed to load skin from pack: %s, resetting skin.".formatted(skinReference.pack()), t);
					Legacyskins.lazyInstance().setSkin(null);
				}
			}
			//? if fabric
			/*return CompletableFuture.completedFuture(null);*/
		}

		//? if fabric {
		/*@Override
		public ResourceLocation getFabricId() {
			return VersionUtils.of(Legacyskins.MOD_ID, "manager");
		}
		*///?}
	}
}
