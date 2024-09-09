//? if yacl {
/*package io.github.redrain0o0.legacyskins.client.screen.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;

public class YaclConfigScreen {
	public YaclConfigScreen() {
		LegacyConfigScreens.CONFIG_SCREENS.put(LegacyConfigScreens.ConfigScreenType.YACL, YaclConfigScreen::create);
	}
	@SuppressWarnings("removal") // it's just set that way so it's not accidentally used
	public static Screen create(Screen parent) {
		YetAnotherConfigLib yacl = YetAnotherConfigLib.createBuilder()
				.title(Component.literal("Legacy Skins"))
				.category(ConfigCategory.createBuilder()
						.name(Component.literal("General"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Show Dev Skins"))
								.binding(false, Legacyskins.INSTANCE::showDevPacks, b -> Legacyskins.INSTANCE.showDevPacks = b)
								.controller(TickBoxControllerBuilder::create)
								.build()
						).option(Option.<Boolean>createBuilder()
								.name(Component.literal("Show Skin Editor Button"))
								.binding(true, Legacyskins.INSTANCE::showSkinEditorButton, b -> Legacyskins.INSTANCE.showEditorButton = b)
								.controller(TickBoxControllerBuilder::create)
								.build()
						).option(Option.<Float>createBuilder()
								.name(Component.literal("Doll X Rotation Limit"))
								.binding(50.0f, Legacyskins.INSTANCE::dollRotationXLimit, f -> Legacyskins.INSTANCE.dollRotationXLimit = f)
								.controller(c -> FloatSliderControllerBuilder.create(c).range(0.0f, 90.0f).step(1.0f))
								.build()
						).option(Option.<M>createBuilder()
								.name(Component.literal("Preferred Config Screen"))
								.binding(M.NONE, M.of(Legacyskins.INSTANCE::configScreenType), t -> Legacyskins.INSTANCE.configScreenType = Optional.ofNullable(t.type))
								.controller(c -> EnumControllerBuilder.create(c).enumClass(M.class))
								.build()
						).build()
				).build();
		return yacl.generateScreen(parent);
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

		public static @NotNull Supplier<@NotNull M> of(Supplier<Optional<LegacyConfigScreens.ConfigScreenType>> b) {
			return () -> Arrays.stream(M.values()).filter(a -> a.type == b.get().orElse(null)).findFirst().orElseThrow();
		}
	}
}
*///?}