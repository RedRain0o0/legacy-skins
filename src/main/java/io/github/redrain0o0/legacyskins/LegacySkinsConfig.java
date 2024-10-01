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
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "DeprecatedIsStillUsed"})
public class LegacySkinsConfig {
	public static final Codec<LegacySkinsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.unboundedMap(UUIDUtil.STRING_CODEC, SkinsConfig.CODEC).fieldOf("profiles").xmap(HashMap::new, LegacySkinsConfig::identity).forGetter(LegacySkinsConfig::getProfiles),
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
	// The ordering of the entries doesn't matter
	private final HashMap<UUID, SkinsConfig> profiles;

	static <T> T identity(T t) {
		return t;
	}

	public HashMap<UUID, SkinsConfig> getProfiles() {
		return profiles;
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
	 * @param screen Classic/Default
	 * @param showDevPacks Whether to show dev packs in the skins screen
	 * @param showEditorButton Whether to show the skin editor button in the title screen
	 * @param dollRotationXLimit The maximum the doll can be rotated along the X axis
	 * @param type Which type of config screen will be preferred
	 */
	public LegacySkinsConfig(HashMap<UUID, SkinsConfig> profiles, SkinsScreen screen, boolean showDevPacks, boolean showEditorButton, float dollRotationXLimit, Optional<LegacyConfigScreens.ConfigScreenType> type) {
		this.profiles = profiles;
		this.screen = screen;
		this.showDevPacks = showDevPacks;
		this.showEditorButton = showEditorButton;
		this.dollRotationXLimit = dollRotationXLimit;
		this.configScreenType = type;
	}

	public static class SkinsConfig {
		public static final Codec<SkinsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				SkinReference.CODEC.optionalFieldOf("selectedSkin").forGetter(SkinsConfig::getCurrentSkin),
				Codec.list(SkinReference.CODEC).xmap(ArrayList::new, c -> c).fieldOf("favorites").forGetter(SkinsConfig::getFavorites)
		).apply(instance, SkinsConfig::new));
		private Optional<SkinReference> currentSkin;
		private ArrayList<SkinReference> favorites;
		public SkinsConfig(Optional<SkinReference> skin, ArrayList<SkinReference> favorites) {
			this.currentSkin = skin;
			this.favorites = favorites;
		}
		public Optional<SkinReference> getCurrentSkin() {
			return currentSkin;
		}
		public ArrayList<SkinReference> getFavorites() {
			return favorites;
		}
	}

	public SkinsConfig getActiveSkinsConfig() {
		UUID uuid = Minecraft.getInstance().getGameProfile().getId();
		return this.profiles.computeIfAbsent(uuid, c -> new SkinsConfig(Optional.empty(), new ArrayList<>()));
	}

	public void setSkin(@Nullable SkinReference skin) {
		SkinsConfig skinsConfig = getActiveSkinsConfig();
		if (skin != null && skin.pack().equals(Constants.DEFAULT_PACK) && skin.ordinal() == 0) {
			skinsConfig.currentSkin = Optional.empty();
			LegacySkinUtils.switchSkin(null);
			return;
		}
		if (skin != null && LegacySkinPack.list.get(skin.pack()) == null) {
			try {
				throw new NullPointerException("Cannot set skin " + skin.pack() + ":" + skin.ordinal() + " because " + skin.pack() + " is null!");
			} catch (NullPointerException e) {
				Legacyskins.LOGGER.error("Attempted to set a skin that has no pack!", e);
				return;
			}
		}
		skinsConfig.currentSkin = Optional.ofNullable(skin);
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
			(Legacyskins.INSTANCE = new LegacySkinsConfig(new HashMap<>(), SkinsScreen.DEFAULT, PlatformUtils.isDevelopmentEnvironment(), false, 50f, Optional.empty())).save();
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
