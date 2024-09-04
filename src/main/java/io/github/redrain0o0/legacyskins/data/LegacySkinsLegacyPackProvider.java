package io.github.redrain0o0.legacyskins.data;

import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacyPackType;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// For API users, use LegacyPackProvider!
public final class LegacySkinsLegacyPackProvider extends LegacyPackProvider {

	public LegacySkinsLegacyPackProvider(PackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
		super(dataOutput, registryLookup /*? if !fabric {*//*, "legacyskins" *//*?}*/);
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
			LegacySkinPack legacySkinPack = new LegacySkinPack(LegacyPackType.DEFAULT, VersionUtils.of(Legacyskins.MOD_ID, "textures/skin_packs/default.png"),
					models.keySet().stream().map(a -> new LegacySkin(VersionUtils.of(Legacyskins.MOD_ID, "skinpacks/default/%s.cpmmodel".formatted(a)))).toList());
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
		builder.addPack(Constants.FAVORITES_PACK, new LegacySkinPack(LegacyPackType.DEFAULT, VersionUtils.of(Legacyskins.MOD_ID, "textures/skin_packs/default.png"), List.of()));
		//Dev pack
		{
			Map<String, String> models = of(
					"redrain0o0", "RedRain0o0",
					"jab125", "Jab125",
					"wilyicaro", "WilyIcaro",
					"tom5454", "tom5454",
					"macandmayo", "MacandMayo",
					"lyn", "Lyn",
					"mintyroad", "Minty Road"
			);
			LegacySkinPack legacySkinPack = new LegacySkinPack(LegacyPackType.DEV, VersionUtils.of(Legacyskins.MOD_ID, "textures/skin_packs/dev.png"),
					models.keySet().stream().map(a -> new LegacySkin(VersionUtils.of(Legacyskins.MOD_ID, "skinpacks/dev/%s.cpmmodel".formatted(a)))).toList());
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

		{
			Map<String, String> models = of(
					"alex", "Alex",
					"ari", "Ari",
					"efe", "Efe",
					"kai", "Kai",
					"makena", "Makena",
					"noor", "Noor",
					"steve", "Steve",
					"sunny", "Sunny",
					"zuri", "Zuri"
			);
			LegacySkinPack legacySkinPack = new LegacySkinPack(LegacyPackType.DEV, VersionUtils.of(Legacyskins.MOD_ID, "textures/skin_packs/dev.png"),
					models.keySet().stream().<LegacySkin>mapMulti((a, b) -> {
						b.accept(new LegacySkin(VersionUtils.of(Legacyskins.MOD_ID, "skinpacks/modern-defaults/wide/%s.cpmmodel".formatted(a))));
						b.accept(new LegacySkin(VersionUtils.of(Legacyskins.MOD_ID, "skinpacks/modern-defaults/slim/%s.cpmmodel".formatted(a))));
					}).toList());
			int i = 0;
			//noinspection unchecked
			for (Map.Entry<String, String> skin : models.entrySet().toArray(Map.Entry[]::new)) {
				int finalI = i;
				LegacySkinsLanguageProvider.addQueuedTranslation(b -> {
					b.add("skin_pack.%s.%s".formatted(builder.id("modern-defaults").toLanguageKey(), finalI), skin.getValue() + " (Wide)");
					b.add("skin_pack.%s.%s".formatted(builder.id("modern-defaults").toLanguageKey(), finalI + 1), skin.getValue() + " (Slim)");
				});
				i+=2;
			}
			builder.addPack("modern-defaults", legacySkinPack);
		}
	}
}
