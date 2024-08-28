package io.github.redrain0o0.legacyskins.client.screen.config;

import com.mojang.serialization.Codec;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import net.minecraft.client.gui.screens.Screen;

import java.util.*;
import java.util.function.Function;

public class LegacyConfigScreens {
	static final LinkedHashMap<ConfigScreenType, Function<Screen, Screen>> CONFIG_SCREENS = new LinkedHashMap<>();
	public static boolean hasConfigScreens() {
		init();
		return !CONFIG_SCREENS.isEmpty();
	}
	private static boolean initialized;
	public static void init() {
		if (initialized) return;
		initialized = true;
		//? if yacl
		if (PlatformUtils.isModLoaded("yet_another_config_lib_v3")) new YaclConfigScreen();
		//? if clothconfig
		if (PlatformUtils.isModLoaded(/*? if fabric {*/"cloth-config" /*?} else {*/ /*"cloth_config" *//*?}*/)) new ClothConfigConfigScreen();
	}
	public static Optional<Screen> createConfigScreen(Screen screen) {
		if (CONFIG_SCREENS.isEmpty()) return Optional.empty();
		ConfigScreenType type;
		if (Legacyskins.INSTANCE.configScreenType().isPresent() && CONFIG_SCREENS.containsKey(type = Legacyskins.INSTANCE.configScreenType().get())) return Optional.ofNullable(CONFIG_SCREENS.get(type).apply(screen));
		return Optional.ofNullable(CONFIG_SCREENS.entrySet().stream().findFirst().orElseThrow().getValue().apply(screen));
	}

	public enum ConfigScreenType {
		YACL,
		CLOTH_CONFIG;
		public static final Codec<ConfigScreenType> CODEC = Codec.STRING.xmap(a -> valueOf(a.toUpperCase(Locale.ROOT)), b -> b.name().toLowerCase(Locale.ROOT));
	}
}
