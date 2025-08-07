//? if figurac {
package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.util.FiguraUtils;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.model.ParentType;
import org.figuramc.figura.model.VanillaModelData;
import org.figuramc.figura.model.rendering.ImmediateAvatarRenderer;
import org.figuramc.figura.model.rendering.PartFilterScheme;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FiguraPlayerSkinQ implements IPlayerSkinQ {
	private final NoPlayerSkinQ whenFailed;
	private final Map<Avatar, ImmediateAvatarRenderer> r = new HashMap<>();
	public FiguraPlayerSkinQ(NoPlayerSkinQ whenFailed) {
		this.whenFailed = whenFailed;
	}
	@Override
	public void render0(PlayerSkinWidget.Model model, @Nullable PlayerSkinWidget.SafeWidget widget, PoseStack stack, LegacySkin playerSkin, MultiBufferSource source, float tickDelta) {
		Avatar avatar = FiguraUtils.loadAvatarNow(playerSkin);
		if (avatar == null) {
			whenFailed.render0(model, widget, stack, playerSkin, source, tickDelta);
			return;
		}
		stack.pushPose();
		//? if !newera
		stack.scale(1, 1, -1);
		stack.translate(0.0F, -1.5F, 0.0F);
		//? if newera
		/*stack.mulPose(Axis.YP.rotationDegrees(180));*/
		ImmediateAvatarRenderer avatarRenderer = null;
		try {
			avatarRenderer = r.computeIfAbsent(FiguraUtils.loadAvatar(FiguraUtils.loadAvatar(playerSkin)), ImmediateAvatarRenderer::new);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		avatarRenderer.bufferSource = source;
		avatarRenderer.currentFilterScheme = PartFilterScheme.MODEL;
		avatarRenderer.setMatrices(stack);
		PlayerModel playerModel = model.slimModel();
		model.setupAnim(widget, playerModel);
		update(avatarRenderer.vanillaModelData, playerModel);
		avatarRenderer.setupRenderer(PartFilterScheme.MODEL, source, stack, tickDelta, LightTexture.pack(15, 15), 1, OverlayTexture.NO_OVERLAY, true, false);
		avatarRenderer.render();
		stack.popPose();
	}

	public void update(VanillaModelData data, PlayerModel entityRenderer) {
		for (Map.Entry<ParentType, VanillaModelData.PartData> entry : data.partMap.entrySet()) {
			ParentType parent = entry.getKey();

			EntityModel<?> vanillaModel;
			vanillaModel = entityRenderer;

			if (vanillaModel == null)
				continue;

			data.update(parent, vanillaModel);
		}
	}
}
//?}