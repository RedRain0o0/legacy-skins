package io.github.redrain0o0.legacyskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import io.github.redrain0o0.legacyskins.migrator.Migrator;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LegacySkinsConfig {
	public static final Codec<LegacySkinsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SkinReference.CODEC.optionalFieldOf("currentSkin").forGetter(LegacySkinsConfig::getCurrentSkin),
			Codec.list(SkinReference.CODEC).xmap(ArrayList::new, c -> c).fieldOf("favorites").forGetter(LegacySkinsConfig::getFavorites),
			SkinsScreen.CODEC.fieldOf("skinsScreen").forGetter(LegacySkinsConfig::getSkinsScreen),
			Codec.BOOL.optionalFieldOf("showDevPacks", false).forGetter(LegacySkinsConfig::showDevPacks)
	).apply(instance, LegacySkinsConfig::new));
	private final SkinsScreen screen;
	private final boolean showDevPacks;
	// selected skin
	public Optional<SkinReference> skin;
	// Note: American English
	/**
	 * @deprecated Use {@link LegacySkinsConfig#getFavorites()}
	 */
	@Deprecated
	public ArrayList<SkinReference> favorites;

	public Optional<SkinReference> getCurrentSkin() {
		return skin;
	}

	public ArrayList<SkinReference> getFavorites() {
		return favorites;
	}

	public SkinsScreen getSkinsScreen() {
		return screen;
	}

	public boolean showDevPacks() {
		return showDevPacks;
	}

	public enum SkinsScreen {
		DEFAULT,
		CLASSIC;
		public static final Codec<SkinsScreen> CODEC = Codec.STRING.xmap(a -> SkinsScreen.valueOf(a.toUpperCase(Locale.ROOT)), a -> a.name().toLowerCase(Locale.ROOT));
	}

	public LegacySkinsConfig(Optional<SkinReference> skin, ArrayList<SkinReference> favorites, SkinsScreen screen, boolean showDevPacks) {
		this.skin = skin;
		this.favorites = favorites;
		this.screen = screen;
		this.showDevPacks = showDevPacks;
	}

	public void setSkin(@Nullable SkinReference skin) {
		if (skin != null && skin.pack().equals(Constants.DEFAULT_PACK) && skin.ordinal() == 0) {
			this.skin = Optional.empty();
			LegacySkinUtils.switchSkin(null);
			return;
		}
		this.skin = Optional.ofNullable(skin);
		LegacySkinUtils.switchSkin(skin != null ? LegacySkinPack.list.get(skin.pack()).skins().get(skin.ordinal()) : null);
	}

	@VisibleForTesting
	public static <T> LegacySkinsConfig fromDynamic(Dynamic<T> dynamic) {
		Dynamic<T> fix = Migrator.CONFIG_FIXER.fix(dynamic);
		return LegacySkinsConfig.CODEC.parse(fix).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
	}

	@VisibleForTesting
	public  <T> Dynamic<T> toDynamic(DynamicOps<T> ops) {
		Dynamic<T> dynamic = new Dynamic<>(ops, CODEC.encodeStart(ops, this).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow());
		return Migrator.CONFIG_FIXER.addSchemaVersion(dynamic);
	}

	public static void load() {
		Path configFile = FabricLoader.getInstance().getConfigDir().resolve("legacyskins.json");
		if (configFile.toFile().isFile()) {
			JsonElement s = null;
			try {
				s = new Gson().fromJson(Files.readString(configFile), JsonElement.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Dynamic<JsonElement> jsonElementDynamic = new Dynamic<>(JsonOps.INSTANCE, s);
			Legacyskins.INSTANCE = fromDynamic(jsonElementDynamic);

		} else {
			new LegacySkinsConfig(Optional.empty(), new ArrayList<>(), SkinsScreen.DEFAULT, FabricLoader.getInstance().isDevelopmentEnvironment() /*TODO make loader agnostic */).save();
		}
	}

	public void save() {
		Path configFile = FabricLoader.getInstance().getConfigDir().resolve("legacyskins.json");
		try {
			JsonOps instance = JsonOps.INSTANCE;
			Dynamic<JsonElement> dynamic = toDynamic(instance);
			Files.writeString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(dynamic.getValue()));
		} catch (IOException e) {
			Legacyskins.LOGGER.error("Failed to save config", e);
		}
	}
}
