package io.github.redrain0o0.legacyskins.data;

import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacyPackType;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// For API users, use LegacyPackProvider!
public final class LegacySkinsLegacyPackProvider extends LegacyPackProvider {

	public LegacySkinsLegacyPackProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup);
	}

	private Map<String, String> of(String... s) {
		String key = null;
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		int i = 0;
		while (i < s.length) {
			if (key == null) {
				key = s[i];
			} else {
				map.put(key, s[i]);
				key = null;
			}
			i++;
		}
		if (key != null) {
			map.put(key, s[s.length-1]);
		}
		return map;
	}

	@Override
	public void addPacks(PackBuilder builder) {
		// Default pack
		{
			Map<String, String> models = of(
					"steve", "Steve",
					"tennissteve", "Tennis Steve",
					"tuxedosteve", "Tuxedo Steve",
					"athletesteve", "Athlete Steve",
					"scottishsteve", "Scottish Steve",
					"prisonersteve", "Prisoner Steve",
					"cycliststeve", "Cyclist Steve",
					"boxersteve", "Boxer Steve",
					"alex", "Alex",
					"tennisalex", "Tennis Alex",
					"tuxedoalex", "Tuxedo Alex",
					"athletealex", "Athlete Alex",
					"swedishalex", "Swedish Alex",
					"prisoneralex", "Prisoner Alex",
					"cyclistalex", "Cyclist Alex",
					"boxeralex", "Boxer Alex",
					"devsteve", "Developer Steve",
					"devalex", "Developer Alex"
			);
			System.out.println(models);
			LegacySkinPack legacySkinPack = new LegacySkinPack(LegacyPackType.DEFAULT, ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "textures/skin_packs/default.png"),
					models.keySet().stream().map(a -> new LegacySkin(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "skinpacks/default/%s.cpmmodel".formatted(a)))).toList());
			int i = 1;
			LegacySkinsLanguageProvider.addQueuedTranslation(b -> b.add("skin_pack.%s.%s".formatted(builder.id("default").toLanguageKey(), 0), "Auto Selected"));
			for (LegacySkin skin : legacySkinPack.skins()) {
				int finalI = i;
				LegacySkinsLanguageProvider.addQueuedTranslation(b -> {
					b.add("skin_pack.%s.%s".formatted(builder.id("default").toLanguageKey(), finalI), (String) models.entrySet().toArray(Map.Entry[]::new)[finalI - 1].getValue());
				});
				i++;
			}
			builder.addPack("default", legacySkinPack);
		}
		// hardcoded
		builder.addPack(Constants.FAVORITES_PACK, new LegacySkinPack(LegacyPackType.DEFAULT, ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "textures/skin_packs/default.png"), List.of()));
		//Dev pack
		{
			Map<String, String> models = of(
					"redrain0o0", "RedRain0o0",
					"jab125", "Jab125"
			);
			LegacySkinPack legacySkinPack = new LegacySkinPack(LegacyPackType.DEV, ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "textures/skin_packs/dev.png"),
					models.keySet().stream().map(a -> new LegacySkin(ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "skinpacks/dev/%s.cpmmodel".formatted(a)))).toList());
			int i = 0;
			for (LegacySkin skin : legacySkinPack.skins()) {
				int finalI = i;
				LegacySkinsLanguageProvider.addQueuedTranslation(b -> {
					b.add("skin_pack.%s.%s".formatted(builder.id("dev").toLanguageKey(), finalI), (String) models.entrySet().toArray(Map.Entry[]::new)[finalI].getValue());
				});
				i++;
			}
			builder.addPack("dev", legacySkinPack);
		}
	}
}
