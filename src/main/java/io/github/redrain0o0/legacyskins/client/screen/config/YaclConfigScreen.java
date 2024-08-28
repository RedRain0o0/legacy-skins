//? if yacl {
package io.github.redrain0o0.legacyskins.client.screen.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class YaclConfigScreen {
	@SuppressWarnings("removal") // it's just set that way so it's not accidentally used
	public static Screen create(Screen parent) {
		YetAnotherConfigLib yacl = YetAnotherConfigLib.createBuilder()
				.title(Component.literal("Legacy Skins"))
				.category(ConfigCategory.createBuilder()
						.name(Component.literal("General"))
						.option(Option.<Boolean>createBuilder()
								.name(Component.literal("Show Dev Skins"))
								.binding(true, Legacyskins.INSTANCE::showDevPacks, b -> Legacyskins.INSTANCE.showDevPacks = b)
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
						).build()
				).build();
		return yacl.generateScreen(parent);
	}
}
//?}