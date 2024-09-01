package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.shared.animation.AnimationEngine;
import io.github.redrain0o0.legacyskins.CPMCompat;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import io.github.redrain0o0.legacyskins.client.util.PlayerSkinUtils;
import io.github.redrain0o0.legacyskins.mixin.PlayerRendererImplAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
//?} else if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
*///?}

public class PlayerSkinWidget extends AbstractWidget {
	private static final float MODEL_OFFSET = 0.0625F;
	private static final float MODEL_HEIGHT = 2.125F;
	private static final float Z_OFFSET = 100.0F;
	private static final float ROTATION_SENSITIVITY = 2.5F;
	private static final float DEFAULT_ROTATION_X = -5.0F;
	private static final float DEFAULT_ROTATION_Y = 30.0F;
	private final float ROTATION_X_LIMIT = Legacyskins.INSTANCE.dollRotationXLimit();
	private final PlayerSkinWidget.Model model;
	final Supplier<SkinReference> skinRef;
	final Supplier<LegacySkin> skin;
	private final int originalWidth;
	private final int originalHeight;
	private float rotationX = 0.0F;//-5.0F;
	private float rotationY = 0.0F;//30.0F;
	public boolean interactable = true;
	private float targetRotationX = Float.NEGATIVE_INFINITY;
	private float targetRotationY = Float.NEGATIVE_INFINITY;
	private float targetPosX = Float.NEGATIVE_INFINITY;
	private float targetPosY = Float.NEGATIVE_INFINITY;
	private float prevPosX = 0;
	private float prevPosY = 0;
	private float prevRotationX = 0;
	private float prevRotationY = 0;
	float progress = 0;
	private float scale = 1;
	private float targetScale = Float.NEGATIVE_INFINITY;
	private float prevScale = 0;

	public PlayerSkinWidget(int width, int height, EntityModelSet entityModelSet, Supplier<SkinReference> supplier) {
		super(0, 0, width, height, CommonComponents.EMPTY);
		originalWidth = width;
		originalHeight = height;
		this.model = PlayerSkinWidget.Model.bake(entityModelSet);
		this.skinRef = supplier;
		this.skin = () -> Optional.ofNullable(LegacySkinPack.list.get(this.skinRef.get().pack())).map(LegacySkinPack::skins).map(a -> a.get(this.skinRef.get().ordinal())).orElse(this.skinRef.get().equals(new SkinReference(Constants.DEFAULT_PACK, 0)) ? null : Constants.FALLBACK_SKIN);
	}

	public boolean isInterpolating() {
		return !(targetRotationX == Float.NEGATIVE_INFINITY && targetRotationY == targetRotationX);
	}

	public void beginInterpolation(float targetRotationX, float targetRotationY, float targetPosX, float targetPosY, float targetScale) {
		this.progress = 0;
		this.start = System.currentTimeMillis();
		this.prevRotationX = rotationX;
		this.prevRotationY = rotationY;
		this.targetRotationX = targetRotationX;
		this.targetRotationY = targetRotationY;
		this.prevPosX = getX();
		this.prevPosY = getY();
		this.targetPosX = targetPosX;
		this.targetPosY = targetPosY;
		this.prevScale = scale;
		this.targetScale = targetScale;
		if(!this.visible || this.wasHidden) {
			this.rotationX = this.targetRotationX;
			this.rotationY = this.targetRotationY;
			this.targetRotationX = Float.NEGATIVE_INFINITY;
			this.targetRotationY = Float.NEGATIVE_INFINITY;
			this.setX((int) this.targetPosX);
			this.setY((int) this.targetPosY);
			this.targetPosX = Float.NEGATIVE_INFINITY;
			this.targetPosY = Float.NEGATIVE_INFINITY;
			this.scale = targetScale;
			setWidth((int) (this.originalWidth * scale));
			//? if <=1.20.1 {
			/*height = (int) (this.originalHeight * scale);
			*///?} else
			setHeight((int) (this.originalHeight * scale));
			this.targetScale = Float.NEGATIVE_INFINITY;
			this.progress = 2;
			if (this.visible) this.wasHidden = false;
		}
	}

	public void visible() {
		this.visible = true;
		//if (wasVisible) return;
		//this.progress = 2;
	}

	boolean wasHidden = true;
	public void invisible() {
		this.wasHidden = true;
		this.visible = false;
		this.progress = 2;
		if (progress >= 1) {
			this.rotationX = this.targetRotationX;
			this.rotationY = this.targetRotationY;
			this.targetRotationX = Float.NEGATIVE_INFINITY;
			this.targetRotationY = Float.NEGATIVE_INFINITY;
			this.setX((int) this.targetPosX);
			this.setY((int) targetPosY);
			this.targetPosX = Float.NEGATIVE_INFINITY;
			this.targetPosY = Float.NEGATIVE_INFINITY;
			this.scale = targetScale;
			setWidth((int) (this.originalWidth * scale));
			//? if <=1.20.1 {
			/*height = (int) (this.originalHeight * scale);
			*///?} else
			setHeight((int) (this.originalHeight * scale));
			this.targetScale = Float.NEGATIVE_INFINITY;
			return;
		}
	}

	public void interpolate(float progress) {
		if (targetRotationX == Float.NEGATIVE_INFINITY && targetRotationY == targetRotationX) return;
		if (progress >= 1) {
			this.rotationX = this.targetRotationX;
			this.rotationY = this.targetRotationY;
			this.targetRotationX = Float.NEGATIVE_INFINITY;
			this.targetRotationY = Float.NEGATIVE_INFINITY;
			this.setX((int) this.targetPosX);
			this.setY((int) targetPosY);
			this.targetPosX = Float.NEGATIVE_INFINITY;
			this.targetPosY = Float.NEGATIVE_INFINITY;
			this.scale = targetScale;
			setWidth((int) (this.originalWidth * scale));
			//? if <=1.20.1 {
			/*height = (int) (this.originalHeight * scale);
			*///?} else
			setHeight((int) (this.originalHeight * scale));
			this.targetScale = Float.NEGATIVE_INFINITY;
			return;
		}
		float x = progress;
		// sin((2πx - π) / 2) + 1) / 2
		float delta = Mth.sin(x*Mth.HALF_PI);//(Mth.sin((2 * Mth.PI * x - Mth.PI) / 2 + 1) / 2);
		float nX = prevRotationX * (1 - delta) + targetRotationX * delta;
		float nY = prevRotationY * (1 - delta) + targetRotationY * delta;
		float nX2 = prevPosX * (1 - delta) + targetPosX * delta;
		float nY2 = prevPosY * (1 - delta) + targetPosY * delta;
		float nS = prevScale * (1 - delta) + targetScale * delta;
		this.rotationX = nX;
		this.rotationY = nY;
		this.setX((int) nX2);
		this.setY((int) nY2);
		this.scale = nS;
		setWidth((int) (this.originalWidth * scale));
		//? if <=1.20.1 {
		/*height = (int) (this.originalHeight * scale);
		*///?} else
		setHeight((int) (this.originalHeight * scale));
	}

	private long start = 0;
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		interpolate(progress);
		progress = (System.currentTimeMillis() - start) / 100f;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate((float)this.getX() + (float)this.getWidth() / 2.0F, (float)(this.getY() + this.getHeight()), Z_OFFSET);
		float g = (float)this.getHeight() / MODEL_HEIGHT;
		guiGraphics.pose().scale(g, g, g);
		guiGraphics.pose().translate(0.0F, -MODEL_OFFSET, 0.0F);
		guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(this.rotationX), 0.0F, -(1 + MODEL_OFFSET), 0.0F);
		guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.rotationY));
		guiGraphics.flush();
		//? if <=1.20.4
		/*Lighting.setupForFlatItems();*/
		//? if >=1.20.6
		Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(this.rotationX));
		this.model.render(guiGraphics, this.skin.get());
		guiGraphics.flush();
		Lighting.setupFor3DItems();
		guiGraphics.pose().popPose();
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		if (isInterpolating()) return;
		if (!interactable) return;
		this.rotationX = Mth.clamp(this.rotationX - (float)deltaY * 2.5F, -ROTATION_X_LIMIT, ROTATION_X_LIMIT);
		this.rotationY += (float)deltaX * ROTATION_SENSITIVITY;
		while (this.rotationY < 0) this.rotationY += 360;
		this.rotationY = (this.rotationY + 180) % 360 - 180;
	}

	@Override
	public void onRelease(double d, double e) {
		//progress = 0;
		//beginInterpolation(0.0F/*-5.0F*/, /*30.0F*/0.0F);
		super.onRelease(d, e);
	}

	@Override
	public void playDownSound(SoundManager soundManager) {
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Nullable
	@Override
	public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
		return null;
	}

	private static final HashMap<String, IClientAPI.PlayerRenderer<net.minecraft.client.model.Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile>> rendererHashMap = new HashMap<>();

	//? if fabric
	@Environment(EnvType.CLIENT)
	//? if neoforge
	/*@OnlyIn(Dist.CLIENT)*/
	static record Model(PlayerModel<?> wideModel, PlayerModel<?> slimModel) {
		public static PlayerSkinWidget.Model bake(EntityModelSet entityModelSet) {
			PlayerModel<?> playerModel = new PlayerModel<>(entityModelSet.bakeLayer(ModelLayers.PLAYER), false);
			PlayerModel<?> playerModel2 = new PlayerModel<>(entityModelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
			playerModel.young = false;
			playerModel2.young = false;
			return new PlayerSkinWidget.Model(playerModel, playerModel2);
		}

		public void render(GuiGraphics guiGraphics, LegacySkin playerSkin) {
			guiGraphics.pose().pushPose();
			guiGraphics.pose().scale(1.0F, 1.0F, -1.0F);
			guiGraphics.pose().translate(0.0F, -1.5F, 0.0F);

			GameProfile gameProfile = Minecraft.getInstance()/*? if <=1.20.1 {*//*.getUser() *//*?}*/.getGameProfile();
			PlayerSkinUtils.F skin = PlayerSkinUtils.skinOf(gameProfile);
			ResourceLocation skinLoc = skin.skinLocation;
			//Minecraft.getInstance().getSkinManager().
			PlayerModel<?> playerModel = playerSkin == null ? skin.slim ? this.slimModel : this.wideModel : this.wideModel;
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
			setupAnim(playerModel);
			if (renderer != null) {
				try {
					renderer.preRender(guiGraphics.bufferSource(), AnimationEngine.AnimationMode.GUI);
				} catch (Throwable t) {
					Legacyskins.LOGGER.error("Error!", t);
				}
			}
			if(renderer == null || renderer.getDefaultTexture() != null) {
				RenderType renderType = null;
				if (renderer != null) {
					renderType = playerModel.renderType(renderer.getDefaultTexture());// playerSkin.texture());
				} else {
					renderType = playerModel.renderType(skinLoc);
				}
				playerModel.renderToBuffer(guiGraphics.pose(), guiGraphics.bufferSource().getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY/*? if <1.21 {*//*, 1.0F, 1.0F, 1.0F, 1.0F*//*?}*/);
				l:
				if (renderer != null && renderer.getDefaultTexture() != null) {
					//CapeLayerMixin
					renderer.prepareSubModel(playerModel, IClientAPI.SubModelType.CAPE, renderer.getDefaultTexture());
					if (renderer.getDefaultTexture().equals(((PlayerRendererImplAccessor) renderer).getTextureMap().get(playerModel))) break l;
					RenderType capeRenderType = renderer.<net.minecraft.client.model.Model>getRenderTypeForSubModel(playerModel); //RenderType.entitySolid(playerSkin.cape().get().texture());
					guiGraphics.pose().pushPose();
					guiGraphics.pose().translate(0.0F, 0.0F, 0.125F);
					PoseStack poseStack = guiGraphics.pose();
					poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + 0 / 2.0F + 0));
					poseStack.mulPose(Axis.ZP.rotationDegrees(0 / 2.0F));
					poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 0 / 2.0F));
					poseStack.mulPose(Axis.XP.rotation((float) ((Math.sin(System.currentTimeMillis() / 1000d) - 1) / 10f)));
					playerModel.renderCloak(guiGraphics.pose(), guiGraphics.bufferSource().getBuffer(capeRenderType), 0xf000f0, OverlayTexture.NO_OVERLAY);
					guiGraphics.pose().popPose();
				}
			}
			if (renderer != null) {
				renderer.postRender();
			}
			guiGraphics.pose().popPose();
		}

		public void setupAnim(PlayerModel<?> model) {
			long l = System.currentTimeMillis();
			model.leftArm.zRot = (float) Math.toRadians(-5);
			model.rightArm.zRot = (float) Math.toRadians(5);
			model.leftArm.xRot = (float) Math.sin(l / 250d) / 5f;
			model.leftLeg.xRot = (float) -Math.sin(l / 250d) / 5f;
			model.rightArm.xRot = (float) -Math.sin(l / 250d) / 5f;
			model.rightLeg.xRot = (float) Math.sin(l / 250d) / 5f;
			model.leftPants.copyFrom(model.leftLeg);
			model.rightPants.copyFrom(model.rightLeg);
			model.leftSleeve.copyFrom(model.leftArm);
			model.rightSleeve.copyFrom(model.rightArm);
			model.jacket.copyFrom(model.body);
		}

//		void a() {
//			IClientAPI.LocalModel localModel = CPMCompat.loadModel(playerSkin.hashCode() + "-temp", LegacySkinUtils.from(playerSkin));
//			MinecraftClientAccess.get().getPlayerRenderManager().getAnimationEngine().handleGuiAnimation(new AnimationHandler(localModel), getSelectedDefinition());
//		}
//		}
	}
}
