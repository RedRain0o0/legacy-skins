//? if clothconfig {
package io.github.redrain0o0.legacyskins.client.screen.config;

import io.github.redrain0o0.legacyskins.Legacyskins;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class ClothConfigConfigScreen {
	public ClothConfigConfigScreen() {
		LegacyConfigScreens.CONFIG_SCREENS.put(LegacyConfigScreens.ConfigScreenType.CLOTH_CONFIG, ClothConfigConfigScreen::create);
	}

	@SuppressWarnings("removal")
	public static Screen create(Screen parent) {
		ConfigBuilder configBuilder = ConfigBuilder.create();
		configBuilder.setParentScreen(parent);
		configBuilder.setTitle(Component.literal("Legacy Skins")).getOrCreateCategory(Component.literal("General"))
				.addEntry(configBuilder.entryBuilder().startBooleanToggle(Component.literal("Show Dev Skins"), Legacyskins.INSTANCE.showDevPacks()).setDefaultValue(Legacyskins.INSTANCE::showDevPacks).setSaveConsumer(a -> Legacyskins.INSTANCE.showDevPacks = a).build())
				.addEntry(configBuilder.entryBuilder().startBooleanToggle(Component.literal("Show Skin Editor Button"), Legacyskins.INSTANCE.showSkinEditorButton()).setDefaultValue(Legacyskins.INSTANCE::showSkinEditorButton).setSaveConsumer(a -> Legacyskins.INSTANCE.showEditorButton = a).build())
				.addEntry(configBuilder.entryBuilder().startFloatField(Component.literal("Doll X Rotation Limit"), Legacyskins.INSTANCE.dollRotationXLimit()).setDefaultValue(Legacyskins.INSTANCE::dollRotationXLimit).setSaveConsumer(a -> Legacyskins.INSTANCE.dollRotationXLimit = a).setMin(0).setMax(90).build())
				.addEntry(configBuilder.entryBuilder().startEnumSelector(Component.literal("Preferred Config Screen"), M.class, M.of(Legacyskins.INSTANCE::configScreenType)).setDefaultValue(M.NONE).setSaveConsumer(a -> Legacyskins.INSTANCE.configScreenType = Optional.ofNullable(a.type)).build());
		return configBuilder.build();
	}

	// Used since you can't have a null enum
	private enum M {
		NONE(null),
		YACL(LegacyConfigScreens.ConfigScreenType.YACL),
		CLOTH_CONFIG(LegacyConfigScreens.ConfigScreenType.CLOTH_CONFIG);
		private final LegacyConfigScreens.ConfigScreenType type;
		M(LegacyConfigScreens.ConfigScreenType type) {
			this.type = type;
		}

		public static M of(Supplier<Optional<LegacyConfigScreens.ConfigScreenType>> b) {
			LegacyConfigScreens.ConfigScreenType configScreenType = b.get().orElse(null);
			return Arrays.stream(M.values()).filter(a -> a.type == configScreenType).findFirst().orElseThrow();
		}
	}
}
//?}