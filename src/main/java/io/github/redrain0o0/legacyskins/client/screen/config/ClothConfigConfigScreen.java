//? if clothconfig {
package io.github.redrain0o0.legacyskins.client.screen.config;

import io.github.redrain0o0.legacyskins.LegacySkins;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
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
				.addEntry(configBuilder.entryBuilder().startBooleanToggle(Component.literal("Show Dev Skins"), LegacySkins.INSTANCE.showDevPacks()).setDefaultValue(LegacySkins.INSTANCE::showDevPacks).setSaveConsumer(a -> LegacySkins.INSTANCE.showDevPacks = a).build())
				.addEntry(configBuilder.entryBuilder().startBooleanToggle(Component.literal("Show Skin Editor Button"), LegacySkins.INSTANCE.showSkinEditorButton()).setDefaultValue(LegacySkins.INSTANCE::showSkinEditorButton).setSaveConsumer(a -> LegacySkins.INSTANCE.showEditorButton = a).build())
				.addEntry(configBuilder.entryBuilder().startFloatField(Component.literal("Doll X Rotation Limit"), LegacySkins.INSTANCE.dollRotationXLimit()).setDefaultValue(LegacySkins.INSTANCE::dollRotationXLimit).setSaveConsumer(a -> LegacySkins.INSTANCE.dollRotationXLimit = a).setMin(0).setMax(90).build())
				.addEntry(configBuilder.entryBuilder().startEnumSelector(Component.literal("Preferred Config Screen"), M.class, M.of(LegacySkins.INSTANCE::configScreenType)).setDefaultValue(M.NONE).setSaveConsumer(a -> LegacySkins.INSTANCE.configScreenType = Optional.ofNullable(a.type)).build())
				.addEntry(configBuilder.entryBuilder().startBooleanToggle(Component.literal("Smooth Interpolation"), !LegacySkins.INSTANCE.choppyLerp()).setDefaultValue(() -> !LegacySkins.INSTANCE.choppyLerp()).setSaveConsumer(a -> LegacySkins.INSTANCE.choppyLerp = !a).build())
				.addEntry(configBuilder.entryBuilder().startTextDescription(Component.literal("Get more skin packs!").withStyle(s -> s.withClickEvent(
						//? if <1.21.5 {
						/*new ClickEvent(ClickEvent.Action.OPEN_URL, "https://github.com/RedRain0o0/legacy-skins/discussions/categories/showcase")
						*///?} else
						new ClickEvent.OpenUrl(java.net.URI.create("https://github.com/RedRain0o0/legacy-skins/discussions/categories/showcase"))
				))).build());
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