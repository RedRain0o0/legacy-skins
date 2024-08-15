package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.math.Axis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.function.Supplier;

import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.animation.AnimationEngine;
import com.tom.cpm.shared.animation.AnimationHandler;
import io.github.redrain0o0.legacyskins.CPMCompat;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class PlayerSkinWidget extends AbstractWidget {
	private static final float MODEL_OFFSET = 0.0625F;
	private static final float MODEL_HEIGHT = 2.125F;
	private static final float Z_OFFSET = 100.0F;
	private static final float ROTATION_SENSITIVITY = 2.5F;
	private static final float DEFAULT_ROTATION_X = -5.0F;
	private static final float DEFAULT_ROTATION_Y = 30.0F;
	private static final float ROTATION_X_LIMIT = 50.0F;
	private final PlayerSkinWidget.Model model;
	private final Supplier<LegacySkin> skin;
	private float rotationX = 0.0F;//-5.0F;
	private float rotationY = 30.0F;
	public boolean interactable = true;
	private float targetRotationX = Float.NEGATIVE_INFINITY;
	private float targetRotationY = Float.NEGATIVE_INFINITY;
	private float prevRotationX = 0;
	private float prevRotationY = 0;
	private float progress = 0;

	public PlayerSkinWidget(int i, int j, EntityModelSet entityModelSet, Supplier<LegacySkin> supplier) {
		super(0, 0, i, j, CommonComponents.EMPTY);
		this.model = PlayerSkinWidget.Model.bake(entityModelSet);
		this.skin = supplier;
	}

	public void beginInterpolation(float targetRotationX, float targetRotationY) {
		this.prevRotationX = rotationX;
		this.prevRotationY = rotationY;
		this.targetRotationX = targetRotationX;
		this.targetRotationY = targetRotationY;
	}

	public void interpolate(float progress) {
		if (targetRotationX == Float.NEGATIVE_INFINITY && targetRotationY == targetRotationX) return;
		if (progress >= 1) {
			this.rotationX = this.targetRotationX;
			this.rotationY = this.targetRotationY;
			this.targetRotationX = Float.NEGATIVE_INFINITY;
			this.targetRotationY = Float.NEGATIVE_INFINITY;
			return;
		}
		float x = progress;
		// sin((2πx - π) / 2) + 1) / 2
		float delta = Mth.sin(x*Mth.HALF_PI);//(Mth.sin((2 * Mth.PI * x - Mth.PI) / 2 + 1) / 2);
		float nX = prevRotationX * (1 - delta) + targetRotationX * delta;
		float nY = prevRotationY * (1 - delta) + targetRotationY * delta;
		this.rotationX = nX;
		this.rotationY = nY;
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		interpolate(progress);
		progress += 0.05f;
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate((float)this.getX() + (float)this.getWidth() / 2.0F, (float)(this.getY() + this.getHeight()), Z_OFFSET);
		float g = (float)this.getHeight() / MODEL_HEIGHT;
		guiGraphics.pose().scale(g, g, g);
		guiGraphics.pose().translate(0.0F, -MODEL_OFFSET, 0.0F);
		guiGraphics.pose().rotateAround(Axis.XP.rotationDegrees(this.rotationX), 0.0F, -(1 + MODEL_OFFSET), 0.0F);
		guiGraphics.pose().mulPose(Axis.YP.rotationDegrees(this.rotationY));
		guiGraphics.flush();
		Lighting.setupForEntityInInventory(Axis.XP.rotationDegrees(this.rotationX));
		this.model.render(guiGraphics, this.skin.get());
		guiGraphics.flush();
		Lighting.setupFor3DItems();
		guiGraphics.pose().popPose();
	}

	@Override
	protected void onDrag(double d, double e, double f, double g) {
		if (!interactable) return;
		this.rotationX = Mth.clamp(this.rotationX - (float)g * 2.5F, -ROTATION_X_LIMIT, ROTATION_X_LIMIT);
		this.rotationY += (float)f * ROTATION_SENSITIVITY;
		System.out.println("DRAGGINH");
	}

	@Override
	public void onRelease(double d, double e) {
		progress = 0;
		beginInterpolation(0.0F/*-5.0F*/, 30.0F);
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

	@Environment(EnvType.CLIENT)
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

			PlayerModel<?> playerModel = this.wideModel;// playerSkin.model() == PlayerSkin.Model.SLIM ? this.slimModel : this.wideModel;
			IClientAPI.PlayerRenderer<net.minecraft.client.model.Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer = rendererHashMap.computeIfAbsent(playerSkin.hashCode() + "-temp", c -> {
				IClientAPI.PlayerRenderer<net.minecraft.client.model.Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer1 = CPMCompat.createRenderer();
				IClientAPI.LocalModel localModel = null;
				try (var f = LegacySkinUtils.from(playerSkin);){
					localModel = CPMCompat.loadModel(playerSkin.hashCode() + "-temp", f);
					renderer1.setLocalModel(localModel);
					//Files.write(Path.of("whyyounotwork.cpmmodel"), LegacySkinUtils.from(playerSkin).readAllBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				System.out.println("Figured out stuff! "+ renderer1 + ", " + localModel);
				return renderer1;
			});

			renderer.setRenderModel(playerModel);
			renderer.setRenderType(RenderType::entityTranslucent);
			setupAnim(playerModel);
			renderer.preRender(guiGraphics.bufferSource(), AnimationEngine.AnimationMode.PLAYER);
			if(renderer.getDefaultTexture() != null) {
				RenderType renderType = playerModel.renderType(renderer.getDefaultTexture());// playerSkin.texture());
				playerModel.renderToBuffer(guiGraphics.pose(), guiGraphics.bufferSource().getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY);
			}
			renderer.postRender();
			guiGraphics.pose().popPose();
		}

		public void setupAnim(PlayerModel<?> model) {
			long l = System.currentTimeMillis();
			model.leftArm.xRot = (float) Math.sin(l / 500d) / 5f;
			model.leftLeg.xRot = (float) Math.sin(l / 500d) / 5f;
			model.rightArm.xRot = (float) -Math.sin(l / 500d) / 5f;
			model.rightLeg.xRot = (float) -Math.sin(l / 500d) / 5f;
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
