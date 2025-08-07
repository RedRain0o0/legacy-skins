package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import org.jetbrains.annotations.Nullable;

public interface IPlayerSkinQ {
	void render0(PlayerSkinWidget.Model model, @Nullable PlayerSkinWidget.SafeWidget widget, PoseStack stack, LegacySkin playerSkin, MultiBufferSource source, float tickDelta);
}
