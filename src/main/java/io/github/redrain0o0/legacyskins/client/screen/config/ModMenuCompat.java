//? if fabric {
/*package io.github.redrain0o0.legacyskins.client.screen.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuCompat implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return screen -> LegacyConfigScreens.createConfigScreen(screen).orElse(null);
	}
}
*///?}