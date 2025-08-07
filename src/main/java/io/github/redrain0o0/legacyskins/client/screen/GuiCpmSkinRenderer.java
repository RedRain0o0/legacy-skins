//? if >=1.21.6 {
/*package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import org.joml.Matrix4fStack;

/// @see net.minecraft.client.gui.render.pip.GuiSkinRenderer
public class GuiCpmSkinRenderer extends PictureInPictureRenderer<GuiCpmSkinRenderState> {
	public GuiCpmSkinRenderer(BufferSource bufferSource) {
		super(bufferSource);
	}

	@Override
	public Class<GuiCpmSkinRenderState> getRenderStateClass() {
		return GuiCpmSkinRenderState.class;
	}

	@Override
	protected void renderToTexture(GuiCpmSkinRenderState guiSkinRenderState, PoseStack poseStack) {
		Minecraft.getInstance().gameRenderer.getLighting().setupFor(Lighting.Entry.PLAYER_SKIN);
		int guiScale = Minecraft.getInstance().getWindow().getGuiScale();
		Matrix4fStack matrix4fStack = RenderSystem.getModelViewStack();
		matrix4fStack.pushMatrix();
		float unscaledYOffset = guiSkinRenderState.scale() * guiScale;
		matrix4fStack.rotateAround(Axis.XP.rotationDegrees(guiSkinRenderState.rotationX()), 0.0F, unscaledYOffset * -(guiSkinRenderState.pivotY()) + PlayerSkinWidget.EXTEND_BY*guiScale, 0.0F);
		matrix4fStack.translate(0, -PlayerSkinWidget.EXTEND_BY*guiScale, 0);
		poseStack.mulPose(Axis.YP.rotationDegrees(-guiSkinRenderState.rotationY()+180/^i have no idea^/));
		//poseStack.translate(0.0F, -1.6010001F, 0.0F);
		//RenderType renderType = guiSkinRenderState.playerModel().slimModel().renderType(guiSkinRenderState.texture());
		LegacySkinsClient.cpmPlayerSkinQ.render0(guiSkinRenderState.playerModel(), guiSkinRenderState.safeWidget(), poseStack, guiSkinRenderState.safeWidget().skin(), this.bufferSource, guiSkinRenderState.delta());
		//guiSkinRenderState.playerModel().renderToBuffer(poseStack, this.bufferSource.getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY);
		this.bufferSource.endBatch();
		matrix4fStack.popMatrix();
	}

	@Override
	protected String getTextureLabel() {
		return "legacy skins cpm player skin";
	}

	private boolean ls$available;
	public void ls$available() {
		this.ls$available = true;
		if (child != null) child.ls$available();
	}

	public void ls$use() {
		this.ls$available = false;
	}

	public boolean ls$isAvailable() {
		return this.ls$available;
	}

	private GuiCpmSkinRenderer child = null;
	public GuiCpmSkinRenderer ls$spawnAnother() {
		if (child == null) {
			child = new GuiCpmSkinRenderer(bufferSource);
			child.ls$available();
		}
		if (!child.ls$isAvailable()) return child.ls$spawnAnother();
		return child;
	}
}
*///?}