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
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;

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
		this.statf = State.NORMKFL;
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
		this.model.render(this, guiGraphics, this.skin.get());
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
	public boolean mouseClicked(double d, double e, int i) {
		if (isInterpolating()  || !interactable || !visible) return false;
		return super.mouseClicked(d, e, i);
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

	public void sktaeChange(SLy s) {
		if (s == SLy.PRFINVING) return; // disable this for now, it's broken
		State statf1 = statf;
		if (s == SLy.STEAKING && statf == State.STEAKING) {
			statf = State.NORMKFL;
		} else if (s == SLy.PRFINVING && statf == State.PCFVUCING) {
			statf = State.NORMKFL;
		} else {
			statf = switch (s) {
				case STEAKING -> State.STEAKING;
				case PRFINVING -> State.PCFVUCING;
			};
		}
		//noinspection ConstantValue
		if (statf1 != statf && statf == State.PCFVUCING) {
			swingTime = 0;
			f = System.currentTimeMillis();
		}
	}

	public enum SLy {
		STEAKING,
		PRFINVING
	}

	private enum State {
		NORMKFL,
		STEAKING,
		PCFVUCING
	}

	private int swingTime;
	private long f = 0;
	private State statf = State.STEAKING;

	static record Model(PlayerModel<?> wideModel, PlayerModel<?> slimModel) {
		public static PlayerSkinWidget.Model bake(EntityModelSet entityModelSet) {
			PlayerModel<?> playerModel = new PlayerModel<>(entityModelSet.bakeLayer(ModelLayers.PLAYER), false);
			PlayerModel<?> playerModel2 = new PlayerModel<>(entityModelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
			playerModel.young = false;
			playerModel2.young = false;
			return new PlayerSkinWidget.Model(playerModel, playerModel2);
		}

		public void render(@Nullable PlayerSkinWidget widget, GuiGraphics guiGraphics, LegacySkin playerSkin) {
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
			setupAnim(widget, playerModel);
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
					poseStack.mulPose(Axis.XP.rotationDegrees(6.0F + 0 / 2.0F + (widget != null && widget.statf == State.STEAKING ? 25.0F : 0)));
					poseStack.mulPose(Axis.ZP.rotationDegrees(0 / 2.0F));
					poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - 0 / 2.0F));
					poseStack.mulPose(Axis.XP.rotation((float) ((Math.sin(System.currentTimeMillis() / 1000d) - 1) / 10f)));
					if (widget != null && widget.statf == State.STEAKING) {
						guiGraphics.pose().translate(0, 1.85F / 16, 1.4F / 16);
					}
					playerModel.renderCloak(guiGraphics.pose(), guiGraphics.bufferSource().getBuffer(capeRenderType), 0xf000f0, OverlayTexture.NO_OVERLAY);
					guiGraphics.pose().popPose();
				}
			}
			if (renderer != null) {
				renderer.postRender();
			}
			guiGraphics.pose().popPose();
		}

		public void setupAnim(@Nullable PlayerSkinWidget widget, PlayerModel<?> model) {
			long l = System.currentTimeMillis();
			model.leftArm.zRot = (float) Math.toRadians(-5);
			model.rightArm.zRot = (float) Math.toRadians(5);
			model.leftArm.xRot = (float) Math.sin(l / 250d) / 5f;
			model.leftLeg.xRot = (float) -Math.sin(l / 250d) / 5f;
			model.rightArm.xRot = (float) -Math.sin(l / 250d) / 5f;
			model.rightLeg.xRot = (float) Math.sin(l / 250d) / 5f;

			if (widget != null && widget.statf == State.STEAKING) {
				model.body.xRot = 0.5F;
				model.rightArm.xRot += 0.4F;
				model.leftArm.xRot += 0.4F;
				model.rightLeg.z = 4.0F;
				model.leftLeg.z = 4.0F;
				model.rightLeg.y = 12.2F;
				model.leftLeg.y = 12.2F;
				model.head.y = 4.2F;
				model.body.y = 3.2F;
				model.leftArm.y = 5.2F;
				model.rightArm.y = 5.2F;


			} else {
				model.body.xRot = 0.0F;
				model.rightLeg.z = 0.0F;
				model.leftLeg.z = 0.0F;
				model.rightLeg.y = 12.0F;
				model.leftLeg.y = 12.0F;
				model.head.y = 0.0F;
				model.body.y = 0.0F;
				model.leftArm.y = 2.0F;
				model.rightArm.y = 2.0F;
			}

			if (widget != null && widget.statf == State.PCFVUCING) {
//				int i = this.getCurrentSwingDuration();
//		if (this.swinging) {
//			this.swingTime++;
//			if (this.swingTime >= i) {
//				this.swingTime = 0;
//				this.swinging = false;
//			}
//		} else {
//			this.swingTime = 0;
//		}
//
//		this.attackAnim = (float)this.swingTime / (float)i;
				int i = 6;
				if (System.currentTimeMillis() % 20 == 0) {
					widget.swingTime++;
					if (widget.swingTime >= i) {
						widget.swingTime = 0;
					}
				}
				float attackAnim = (float)widget.swingTime / i;
				model.attackTime = attackAnim;
				System.out.println("f" + widget.swingTime + "e" + attackAnim);
				//model.attackTime = ((System.currentTimeMillis() - widget.stateStart) / 1000f * 20) - 1;// - (System.currentTimeMillis() - widget.stateStart) / 10000f;
				//if (((System.currentTimeMillis() - widget.stateStart) / 100f) >= 3) widget.stateStart = System.currentTimeMillis() + 100;
				setupAttackAnimation(model, 0);
			} else {
				model.attackTime = 0;
				model.rightArm.yRot = 0;
				//model.rightArm.xRot = 0;
				//model.leftArm.xRot = 0;
				model.leftArm.yRot = 0;
			}

			model.leftPants.copyFrom(model.leftLeg);
			model.rightPants.copyFrom(model.rightLeg);
			model.leftSleeve.copyFrom(model.leftArm);
			model.rightSleeve.copyFrom(model.rightArm);
			model.jacket.copyFrom(model.body);

			//model.setupAttackAnimation()
		}

		protected void setupAttackAnimation(PlayerModel<?> model, float f) {
			if (!(model.attackTime <= 0.0F)) {
				//AbstractClientPlayer
				HumanoidArm humanoidArm = HumanoidArm.RIGHT;
				//noinspection ConstantValue
				ModelPart modelPart = humanoidArm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
				float g = model.attackTime;
				model.body.yRot = Mth.sin(Mth.sqrt(g) * (float) (Math.PI * 2)) * 0.2F;
				//noinspection ConstantValue
				if (humanoidArm == HumanoidArm.LEFT) {
					model.body.yRot *= -1.0F;
				}

				model.rightArm.z = Mth.sin(model.body.yRot) * 5.0F;
				model.rightArm.x = -Mth.cos(model.body.yRot) * 5.0F;
				model.leftArm.z = -Mth.sin(model.body.yRot) * 5.0F;
				model.leftArm.x = Mth.cos(model.body.yRot) * 5.0F;
				model.rightArm.yRot = model.rightArm.yRot + model.body.yRot;
				model.leftArm.yRot = model.leftArm.yRot + model.body.yRot;
				model.leftArm.xRot = model.leftArm.xRot + model.body.yRot;
				g = 1.0F - model.attackTime;
				g *= g;
				g *= g;
				g = 1.0F - g;
				float h = Mth.sin(g * (float) Math.PI);
				float i = Mth.sin(model.attackTime * (float) Math.PI) * -(model.head.xRot - 0.7F) * 0.75F;
				modelPart.xRot -= h * 1.2F + i;
				modelPart.yRot = modelPart.yRot + model.body.yRot * 2.0F;
				modelPart.zRot = modelPart.zRot + Mth.sin(model.attackTime * (float) Math.PI) * -0.4F;
			}
		}

//		void a() {
//			IClientAPI.LocalModel localModel = CPMCompat.loadModel(playerSkin.hashCode() + "-temp", LegacySkinUtils.from(playerSkin));
//			MinecraftClientAccess.get().getPlayerRenderManager().getAnimationEngine().handleGuiAnimation(new AnimationHandler(localModel), getSelectedDefinition());
//		}
//		}
	}
}
