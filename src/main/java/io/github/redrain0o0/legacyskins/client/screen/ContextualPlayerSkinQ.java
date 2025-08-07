package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

public class ContextualPlayerSkinQ implements IPlayerSkinQ {

	private final IPlayerSkinQ cpm;
	private final IPlayerSkinQ figura;

	public ContextualPlayerSkinQ(IPlayerSkinQ cpm, IPlayerSkinQ figura) {
		this.cpm = cpm;
		this.figura = figura;
	}
	@Override
	public void render0(PlayerSkinWidget.Model model, @Nullable PlayerSkinWidget.SafeWidget widget, PoseStack stack, LegacySkin playerSkin, MultiBufferSource source, float tickDelta) {
		if (playerSkin == null || playerSkin.type() == LegacySkin.Type.CPM) cpm.render0(model, widget, stack, playerSkin, source, tickDelta);
		else if (playerSkin.type() == LegacySkin.Type.FIGURA) figura.render0(model, widget, stack, playerSkin, source, tickDelta);
	}
}
