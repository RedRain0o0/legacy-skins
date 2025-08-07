package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.shared.animation.AnimationEngine;
import io.github.redrain0o0.legacyskins.CPMCompat;
import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import io.github.redrain0o0.legacyskins.client.util.PlayerSkinUtils;
import io.github.redrain0o0.legacyskins.mixin.PlayerRendererImplAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;

public class CPMPlayerSkinQ implements IPlayerSkinQ {
	private final HashMap<String, IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile>> rendererHashMap = new HashMap<>();

	@Override
	public void render0(PlayerSkinWidget.Model model, @Nullable PlayerSkinWidget.SafeWidget widget, PoseStack stack, LegacySkin playerSkin, MultiBufferSource source, float tickDelta) {
		stack.pushPose();
		stack.scale(1.0F, 1.0F, -1.0F);
		stack.translate(0.0F, -1.5F, 0.0F);

		GameProfile gameProfile = Minecraft.getInstance()/*? if <=1.20.1 {*//*.getUser() *//*?}*/.getGameProfile();
		PlayerSkinUtils.F skin = PlayerSkinUtils.skinOf(gameProfile);
		ResourceLocation skinLoc = skin.skinLocation;
		//Minecraft.getInstance().getSkinManager().
		PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ playerModel = playerSkin == null ? skin.slim ? model.slimModel() : model.wideModel() : model.wideModel();
		IClientAPI.PlayerRenderer<net.minecraft.client.model.Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer = null;
		if (playerSkin != null) {
			renderer = rendererHashMap.computeIfAbsent(playerSkin.hashCode() + "-temp", c -> {
				IClientAPI.PlayerRenderer<net.minecraft.client.model.Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer1 = CPMCompat.createRenderer();
				IClientAPI.LocalModel localModel = null;
				try (var f = LegacySkinUtils.from(playerSkin);) {
					localModel = CPMCompat.loadModel(playerSkin.hashCode() + "-temp", f);
					renderer1.setLocalModel(localModel);
					//Files.write(Path.of("whyyounotwork.cpmmodel"), LegacySkinUtils.from(playerSkin).readAllBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				//System.out.println("Figured out stuff! "+ renderer1 + ", " + localModel);
				return renderer1;
			});
		}

		if (renderer != null) {
			renderer.setRenderModel(playerModel);
			renderer.setRenderType(RenderType::entityTranslucent);
		}
		if (renderer != null) {
			try {
				renderer.preRender(source, AnimationEngine.AnimationMode.GUI);
			} catch (Throwable t) {
				LegacySkins.LOGGER.error("Error!", t);
			}
		}
		model.setupAnim(widget, playerModel);
		if(renderer == null || renderer.getDefaultTexture() != null) {
			RenderType renderType = null;
			if (renderer != null) {
				renderType = playerModel.renderType(renderer.getDefaultTexture());// playerSkin.texture());
			} else {
				renderType = playerModel.renderType(skinLoc);
			}
			playerModel.renderToBuffer(stack, source.getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY/*? if <1.21 {*//*, 1.0F, 1.0F, 1.0F, 1.0F*//*?}*/);
			l:
			if (renderer != null && renderer.getDefaultTexture() != null) {
				//CapeLayerMixin
				renderer.prepareSubModel(playerModel, IClientAPI.SubModelType.CAPE, renderer.getDefaultTexture());
				if (renderer.getDefaultTexture().equals(((PlayerRendererImplAccessor) renderer).getTextureMap().get(playerModel))) break l;
				RenderType capeRenderType = renderer.<net.minecraft.client.model.Model>getRenderTypeForSubModel(playerModel); //RenderType.entitySolid(playerSkin.cape().get().texture());
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
			} else if (renderer == null) {
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
		}
		if (renderer != null) {
			renderer.postRender();
		}
		stack.popPose();
	}
}
