//? if >=1.21.6 {
/*package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.authlib.GameProfile;
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
import io.github.redrain0o0.legacyskins.LegacySkins;
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
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
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

/// @see net.minecraft.client.gui.components.PlayerSkinWidget
public class PlayerSkinWidget extends AbstractWidget {
	private static final float MODEL_OFFSET = 0.0625F;
	private static final float MODEL_HEIGHT = 2.125F;
	private static final float Z_OFFSET = 100.0F;
	private static final float ROTATION_SENSITIVITY = 2.5F;
	private static final float DEFAULT_ROTATION_X = -5.0F;
	private static final float DEFAULT_ROTATION_Y = 30.0F;
	private final float ROTATION_X_LIMIT = LegacySkins.INSTANCE.dollRotationXLimit();
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
	private boolean overrideVisible = true;

	public PlayerSkinWidget(int width, int height, EntityModelSet entityModelSet, Supplier<SkinReference> supplier) {
		super(-9999, -9999, width, height, CommonComponents.EMPTY);
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

	public void overrideVisible(boolean overrideVisible) {
		this.overrideVisible = overrideVisible;
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
			setHeight((int) (this.originalHeight * scale));
			this.targetScale = Float.NEGATIVE_INFINITY;
			return;
		}
		float x = progress;
		// sin((2πx - π) / 2) + 1) / 2
		float delta = x;//Mth.sin(x*Mth.HALF_PI);//(Mth.sin((2 * Mth.PI * x - Mth.PI) / 2 + 1) / 2);
		// access field directly since it's faster that way
		//noinspection removal
		if (LegacySkins.INSTANCE.choppyLerp) {
			delta /= 0.2f;
			delta = Math.round(delta);
			delta = delta * 0.2f;
		}
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
		setHeight((int) (this.originalHeight * scale));
	}

	private long start = 0;

//	protected void renderWidgetPre(GuiGraphics guiGraphics, int i, int j, float f) {
//		guiGraphics.pose().pushPose();
//		guiGraphics.pose().translate((float)this.getX() + (float)this.getWidth() / 2.0F, (float)(this.getY() + this.getHeight()), 100.0F);
//		float g = (float)this.getHeight() / 2.125F;
//		guiGraphics.pose().scale(g, g, g);
//		guiGraphics.pose().translate(0.0F, -0.0625F, 0.0F);
//		guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(this.rotationX), 0.0F, -1.0625F, 0.0F);
//		guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.rotationY));
//		guiGraphics.flush();
//		Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(this.rotationX));
//		this.model.render(guiGraphics, (PlayerSkin)this.skin.get());
//		guiGraphics.flush();
//		Lighting.setupFor3DItems();
//		guiGraphics.pose().popPose();
//	}

//	protected void renderWidgetNew(GuiGraphics guiGraphics, int i, int j, float f) {
//		float g = 0.97F * (float)this.getHeight() / 2.125F;
//		float h = -1.0625F;
//		PlayerSkin playerSkin = (PlayerSkin)this.skin.get();
//		PlayerModel playerModel = playerSkin.model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;
//		guiGraphics.submitSkinRenderState(playerModel, playerSkin.texture(), g, this.rotationX, this.rotationY, -1.0625F, this.getX(), this.getY(), this.getRight(), this.getBottom());
//	}

	public static final int EXTEND_BY = 50;
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		interpolate(progress);
		progress = (System.currentTimeMillis() - start) / 200f;
		ScreenRectangle a = guiGraphics./^? if neoforge {^//^peekScissorStack()^//^?} else {^/scissorStack.peek()/^?}^/;
		//a = a == null ? null : new ScreenRectangle(a.position().x()-extendBy, a.position().y()-extendBy, a.width()+extendBy*2, a.height()+extendBy*2);
		float g = (float)this.getHeight() / MODEL_HEIGHT;
		guiGraphics./^? if neoforge {^//^submitPictureInPictureRenderState^//^?} else {^/guiRenderState.submitPicturesInPictureState/^?}^/(new GuiCpmSkinRenderState(new SafeWidget(statf, skin.get()), this.model, this.rotationX, this.rotationY, -1.0625F, this.getX()-EXTEND_BY, this.getY()-EXTEND_BY, this.getRight()+EXTEND_BY, this.getBottom()+EXTEND_BY, g, a,
				//a
				PictureInPictureRenderState.getBounds(this.getX()-EXTEND_BY, this.getY()-EXTEND_BY, this.getRight()+EXTEND_BY, this.getBottom()+EXTEND_BY, a)
				, f));
		//guiGraphics.submitSkinRenderState();
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
		if (isInterpolating() || !interactable || !visible) return false;
		return super.mouseClicked(d, e, i);
	}

	@Override
	public void onRelease(double d, double e) {
		//progress = 0;
		//beginInterpolation(0.0F/^-5.0F^/, /^30.0F^/0.0F);
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

	enum State {
		NORMKFL,
		STEAKING,
		PCFVUCING
	}

	private int swingTime;
	private long f = 0;
	private State statf = State.STEAKING;

	record SafeWidget(State statf, LegacySkin skin) {

	}
	static record Model(PlayerModel wideModel, PlayerModel slimModel , PlayerCapeModel<?> playerCapeModel) {
		public static PlayerSkinWidget.Model bake(EntityModelSet entityModelSet) {
			PlayerModel playerModel = new PlayerModel(entityModelSet.bakeLayer(ModelLayers.PLAYER), false);
			PlayerModel playerModel2 = new PlayerModel(entityModelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
			PlayerCapeModel<?> playerCapeModel = new PlayerCapeModel<>(entityModelSet.bakeLayer(ModelLayers.PLAYER_CAPE));
			PlayerRenderState renderState = new PlayerRenderState();
			((PlayerCapeModel) playerCapeModel).setupAnim(renderState);
			return new PlayerSkinWidget.Model(playerModel, playerModel2, playerCapeModel);
		}

		public void setupNewAnim(@Nullable SafeWidget widget, PlayerModel model) {
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
			model.copyPropertiesTo((HumanoidModel<PlayerRenderState>) playerCapeModel);
		}

		public void setupAnim(@Nullable SafeWidget widget, PlayerModel model) {
			setupNewAnim(widget, model);
		}
	}
}
*///?} else {
package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Supplier;

import io.github.redrain0o0.legacyskins.CPMCompat;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import io.github.redrain0o0.legacyskins.client.util.PlayerSkinUtils;
import io.github.redrain0o0.legacyskins.mixin.PlayerRendererImplAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
//? if >=1.21.2 {
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerCapeModel;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
//?}
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
	private final float ROTATION_X_LIMIT = LegacySkins.INSTANCE.dollRotationXLimit();
	private final Model model;
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
	private boolean overrideVisible = true;

	public PlayerSkinWidget(int width, int height, EntityModelSet entityModelSet, Supplier<SkinReference> supplier) {
		super(-9999, -9999, width, height, CommonComponents.EMPTY);
		originalWidth = width;
		originalHeight = height;
		this.model = Model.bake(entityModelSet);
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

	public void overrideVisible(boolean overrideVisible) {
		this.overrideVisible = overrideVisible;
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
		float delta = x;//Mth.sin(x*Mth.HALF_PI);//(Mth.sin((2 * Mth.PI * x - Mth.PI) / 2 + 1) / 2);
		// access field directly since it's faster that way
		//noinspection removal
		if (LegacySkins.INSTANCE.choppyLerp) {
			delta /= 0.2f;
			delta = Math.round(delta);
			delta = delta * 0.2f;
		}
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
		progress = (System.currentTimeMillis() - start) / 200f;
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
		this.model.render(this, guiGraphics, this.skin.get(), f);
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
		if (isInterpolating() || !interactable || !visible) return false;
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

	enum State {
		NORMKFL,
		STEAKING,
		PCFVUCING
	}

	private int swingTime;
	private long f = 0;
	State statf = State.STEAKING;

	record SafeWidget(State statf, LegacySkin skin) {

	}

	static record Model(PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ wideModel, PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ slimModel /*? if >=1.21.3 {*/, PlayerCapeModel<?> playerCapeModel /*?}*/) {
		public static Model bake(EntityModelSet entityModelSet) {
			PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ playerModel = new PlayerModel/*? if <1.21.2 {*//*<>*//*?}*/(entityModelSet.bakeLayer(ModelLayers.PLAYER), false);
			PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ playerModel2 = new PlayerModel/*? if <1.21.2 {*//*<>*//*?}*/(entityModelSet.bakeLayer(ModelLayers.PLAYER_SLIM), true);
			//? if >=1.21.2 {
			PlayerCapeModel<?> playerCapeModel = new PlayerCapeModel<>(entityModelSet.bakeLayer(ModelLayers.PLAYER_CAPE));
			PlayerRenderState renderState = new PlayerRenderState();
			((PlayerCapeModel) playerCapeModel).setupAnim(renderState);
			//?} elif <1.21.2 {
			/*playerModel.young = false;
			playerModel2.young = false;
			*///?}
			return new Model(playerModel, playerModel2 /*? if >=1.21.2 {*/, playerCapeModel/*?}*/);
		}

		public void render(@Nullable PlayerSkinWidget widget, GuiGraphics guiGraphics, LegacySkin playerSkin, float tickDelta) {
			//? if <1.21.2 {
			/*LegacySkinsClient.cpmPlayerSkinQ.render0(this, new SafeWidget(widget.statf, widget.skin.get()), guiGraphics.pose(), playerSkin, guiGraphics.bufferSource(), tickDelta);
			*///?} else
			guiGraphics.drawSpecial(source -> LegacySkinsClient.cpmPlayerSkinQ.render0(this, new SafeWidget(widget.statf, widget.skin.get()), guiGraphics.pose(), playerSkin, source, tickDelta));
		}

		//? if >=1.21.2 {
		public void setupNewAnim(@Nullable PlayerSkinWidget.SafeWidget widget, PlayerModel model) {
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
			model.copyPropertiesTo((HumanoidModel<PlayerRenderState>) playerCapeModel);
		}
		//?}

		public void setupAnim(@Nullable PlayerSkinWidget.SafeWidget widget, PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ model) {
			//? if >=1.21.2
			if (true) { setupNewAnim(widget, model); return; }
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

			/*
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
			 */

			model.leftPants.copyFrom(model.leftLeg);
			model.rightPants.copyFrom(model.rightLeg);
			model.leftSleeve.copyFrom(model.leftArm);
			model.rightSleeve.copyFrom(model.rightArm);
			model.jacket.copyFrom(model.body);
			model.hat.copyFrom(model.head);

			//model.setupAttackAnimation()
		}


//		protected void setupAttackAnimation(PlayerModel/*? if <1.21.2 {*//*<?>*//*?}*/ model, float f) {
//			if (!(model.attackTime <= 0.0F)) {
//				//AbstractClientPlayer
//				HumanoidArm humanoidArm = HumanoidArm.RIGHT;
//				//noinspection ConstantValue
//				ModelPart modelPart = humanoidArm == HumanoidArm.RIGHT ? model.rightArm : model.leftArm;
//				float g = model.attackTime;
//				model.body.yRot = Mth.sin(Mth.sqrt(g) * (float) (Math.PI * 2)) * 0.2F;
//				//noinspection ConstantValue
//				if (humanoidArm == HumanoidArm.LEFT) {
//					model.body.yRot *= -1.0F;
//				}
//
//				model.rightArm.z = Mth.sin(model.body.yRot) * 5.0F;
//				model.rightArm.x = -Mth.cos(model.body.yRot) * 5.0F;
//				model.leftArm.z = -Mth.sin(model.body.yRot) * 5.0F;
//				model.leftArm.x = Mth.cos(model.body.yRot) * 5.0F;
//				model.rightArm.yRot = model.rightArm.yRot + model.body.yRot;
//				model.leftArm.yRot = model.leftArm.yRot + model.body.yRot;
//				model.leftArm.xRot = model.leftArm.xRot + model.body.yRot;
//				g = 1.0F - model.attackTime;
//				g *= g;
//				g *= g;
//				g = 1.0F - g;
//				float h = Mth.sin(g * (float) Math.PI);
//				float i = Mth.sin(model.attackTime * (float) Math.PI) * -(model.head.xRot - 0.7F) * 0.75F;
//				modelPart.xRot -= h * 1.2F + i;
//				modelPart.yRot = modelPart.yRot + model.body.yRot * 2.0F;
//				modelPart.zRot = modelPart.zRot + Mth.sin(model.attackTime * (float) Math.PI) * -0.4F;
//			}
//		}

//		void a() {
//			IClientAPI.LocalModel localModel = CPMCompat.loadModel(playerSkin.hashCode() + "-temp", LegacySkinUtils.from(playerSkin));
//			MinecraftClientAccess.get().getPlayerRenderManager().getAnimationEngine().handleGuiAnimation(new AnimationHandler(localModel), getSelectedDefinition());
//		}
//		}
	}
}
//?}