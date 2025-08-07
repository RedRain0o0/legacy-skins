//? if >=1.21.6 {
/*package io.github.redrain0o0.legacyskins.client.screen;

import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record GuiCpmSkinRenderState(
	@Nullable io.github.redrain0o0.legacyskins.client.screen.PlayerSkinWidget.SafeWidget safeWidget,
	PlayerSkinWidget.Model playerModel,
	//PlayerSkin skin,
	//ResourceLocation texture,
	float rotationX,
	float rotationY,
	float pivotY,
	int x0,
	int y0,
	int x1,
	int y1,
	float scale,
	@Nullable ScreenRectangle scissorArea,
	@Nullable ScreenRectangle bounds,
	float delta
) implements PictureInPictureRenderState {

}
*///?}