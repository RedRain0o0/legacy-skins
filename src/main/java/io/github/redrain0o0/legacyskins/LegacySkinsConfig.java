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
import io.github.redrain0o0.legacyskins.client.screen.config.LegacyConfigScreens;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import io.github.redrain0o0.legacyskins.migrator.Migrator;
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "DeprecatedIsStillUsed"})
public class LegacySkinsConfig {
	public static final Codec<LegacySkinsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SkinReference.CODEC.optionalFieldOf("currentSkin").forGetter(LegacySkinsConfig::getCurrentSkin),
			Codec.list(SkinReference.CODEC).xmap(ArrayList::new, c -> c).fieldOf("favorites").forGetter(LegacySkinsConfig::getFavorites),
			SkinsScreen.CODEC.fieldOf("skinsScreen").forGetter(LegacySkinsConfig::getSkinsScreen),
			Codec.BOOL.fieldOf("showDevPacks").forGetter(LegacySkinsConfig::showDevPacks),
			Codec.BOOL.fieldOf("showSkinEditorButton").forGetter(LegacySkinsConfig::showSkinEditorButton),
			Codec.FLOAT.optionalFieldOf("dollRotationXLimit", 50f).forGetter(LegacySkinsConfig::dollRotationXLimit),
			LegacyConfigScreens.ConfigScreenType.CODEC.optionalFieldOf("configScreen").forGetter(LegacySkinsConfig::configScreenType)
	).apply(instance, LegacySkinsConfig::new));
	private final SkinsScreen screen;
	@Deprecated(forRemoval = true)
	public boolean showDevPacks;
	@Deprecated(forRemoval = true)
	public boolean showEditorButton;
	@Deprecated(forRemoval = true)
	public float dollRotationXLimit;
	@Deprecated(forRemoval = true)
	public Optional<LegacyConfigScreens.ConfigScreenType> configScreenType;
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

	public boolean showSkinEditorButton() {
		return showEditorButton;
	}

	public float dollRotationXLimit() {
		return dollRotationXLimit;
	}

	public Optional<LegacyConfigScreens.ConfigScreenType> configScreenType() {
		return configScreenType;
	}

	public enum SkinsScreen {
		DEFAULT,
		CLASSIC;
		public static final Codec<SkinsScreen> CODEC = Codec.STRING.xmap(a -> SkinsScreen.valueOf(a.toUpperCase(Locale.ROOT)), a -> a.name().toLowerCase(Locale.ROOT));
	}

	/**
	 * @param skin A reference to a skin
	 * @param favorites A list of {@link SkinReference}s
	 * @param screen Classic/Default
	 * @param showDevPacks Whether to show dev packs in the skins screen
	 * @param showEditorButton Whether to show the skin editor button in the title screen
	 * @param dollRotationXLimit The maximum the doll can be rotated along the X axis
	 * @param type Which type of config screen will be preferred
	 */
	public LegacySkinsConfig(Optional<SkinReference> skin, ArrayList<SkinReference> favorites, SkinsScreen screen, boolean showDevPacks, boolean showEditorButton, float dollRotationXLimit, Optional<LegacyConfigScreens.ConfigScreenType> type) {
		this.skin = skin;
		this.favorites = favorites;
		this.screen = screen;
		this.showDevPacks = showDevPacks;
		this.showEditorButton = showEditorButton;
		this.dollRotationXLimit = dollRotationXLimit;
		this.configScreenType = type;
	}

	public void setSkin(@Nullable SkinReference skin) {
		if (skin != null && skin.pack().equals(Constants.DEFAULT_PACK) && skin.ordinal() == 0) {
			this.skin = Optional.empty();
			LegacySkinUtils.switchSkin(null);
			return;
		}
		if (skin != null && LegacySkinPack.list.get(skin.pack()) == null) {
			try {
				throw new NullPointerException("Cannot set skin " + skin.pack() + ":" + skin.ordinal() + " because " + skin.pack() + " is null!");
			} catch (Throwable t) {
				Legacyskins.LOGGER.error("Attempted to set a skin that has no pack!", t);
				return;
			}
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
	public <T> Dynamic<T> toDynamic(DynamicOps<T> ops) {
		Dynamic<T> dynamic = new Dynamic<>(ops, CODEC.encodeStart(ops, this).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow());
		return Migrator.CONFIG_FIXER.addSchemaVersion(dynamic);
	}

	public static void load() {
		Path configFile = PlatformUtils.getConfigDir().resolve("legacyskins.json");
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
			(Legacyskins.INSTANCE = new LegacySkinsConfig(Optional.empty(), new ArrayList<>(), SkinsScreen.DEFAULT, PlatformUtils.isDevelopmentEnvironment() /*TODO make loader agnostic */, false, 50f, Optional.empty())).save();
		}
	}

	public void save() {
		Path configFile = PlatformUtils.getConfigDir().resolve("legacyskins.json");
		try {
			JsonOps instance = JsonOps.INSTANCE;
			Dynamic<JsonElement> dynamic = toDynamic(instance);
			Files.writeString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(dynamic.getValue()));
		} catch (IOException e) {
			Legacyskins.LOGGER.error("Failed to save config", e);
		}
	}
}
