package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.util.PlayerSkinUtils;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class NoPlayerSkinQ implements IPlayerSkinQ {
	@Override
	public void render0(PlayerSkinWidget.Model model, @Nullable PlayerSkinWidget.SafeWidget widget, PoseStack stack, LegacySkin playerSkin, MultiBufferSource source, float tickDelta) {
		stack.pushPose();
		stack.scale(1.0F, 1.0F, -1.0F);
		stack.translate(0.0F, -1.5F, 0.0F);

		ResourceLocation skinLoc;
		PlayerSkinUtils.F skin;
		if (playerSkin == null) {
			GameProfile gameProfile = Minecraft.getInstance()/*? if <=1.20.1 {*//*.getUser() *//*?}*/.getGameProfile();
			skin = PlayerSkinUtils.skinOf(gameProfile);
			skinLoc = skin.skinLocation;
		} else {
			skinLoc = VersionUtils.of(LegacySkins.MOD_ID, "textures/placeholder.png");
			skin = new PlayerSkinUtils.F( Minecraft.getInstance()/*? if <=1.20.1 {*//*.getUser() *//*?}*/.getGameProfile());
			skin.slim = false;
		}
		//Minecraft.getInstance().getSkinManager().
		PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ playerModel = playerSkin == null ? skin.slim ? model.slimModel() : model.wideModel() : model.wideModel();

		model.setupAnim(widget, playerModel);
		RenderType renderType = null;
		renderType = playerModel.renderType(skinLoc);
		playerModel.renderToBuffer(stack, source.getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY/*? if <1.21 {*//*, 1.0F, 1.0F, 1.0F, 1.0F*//*?}*/);
		l:
		{
			if (skin.capeLocation != null) {
				RenderType capeRenderType = RenderType.entityTranslucent(skin.capeLocation); // even though Minecraft uses entitySolid, we use entityTranslucent because many cape mods make it translucent
				stack.pushPose();
				stack.pushPose();
				/*? if <1.21.2*//*stack.translate(0.0F, 0.0F, 0.125F);*/
				PoseStack poseStack = stack;
				poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + 0 / 2.0F + (widget != null && widget.statf() == PlayerSkinWidget.State.STEAKING ? 25.0F : 0)));
				/*? if <1.21.2*//*poseStack.mulPose(Axis.ZP.rotationDegrees(0 / 2.0F));*/
				/*? if <1.21.2*//*poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 0 / 2.0F));*/
				poseStack.mulPose(Axis.XP.rotation((float) ((Math.sin(System.currentTimeMillis() / 1000d) - 1) / 10f)));
				if (widget != null && widget.statf() == PlayerSkinWidget.State.STEAKING) {
					stack.translate(0, 1.85F / 16, /*? if >=1.21.2 {*/0/*?} else {*//*1.4F / 16*//*?}*/);
				}
				//? if <1.21.2 {
				/*playerModel.renderCloak
				 *///?} else
				model.playerCapeModel().renderToBuffer
						(stack, source.getBuffer(capeRenderType), 0xf000f0, OverlayTexture.NO_OVERLAY);
				stack.popPose();
				stack.popPose();
			}
		}
		stack.popPose();
	}
}
