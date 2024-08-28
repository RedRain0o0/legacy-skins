package io.github.redrain0o0.legacyskins.client.screen.config;

import net.minecraft.client.gui.screens.Screen;

import java.util.Optional;

public class LegacyConfigScreens {
	public static Optional<Screen> createConfigScreen(Screen screen) {
		//? if yacl {
		return Optional.of(YaclConfigScreen.create(screen));
		//?} else
		/*return Optional.empty();*/
	}
}
