package io.github.redrain0o0.legacyskins.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class EScreen extends Screen {
	public static int ticky = 0;
	private final Screen parent;

	public EScreen(Screen parent) {
		super(Component.empty());
		this.parent = parent;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(width / 2, height / 2, 0);
		guiGraphics.pose().scale(5, 5, 0);
		guiGraphics.drawString(Minecraft.getInstance().font, "E", 0, 0, 0xffffffff);
		guiGraphics.pose().popPose();
	}

	@Override
	public void tick() {
		super.tick();
		ticky++;
		if (ticky > 15) {
			Minecraft.getInstance().setScreen(parent);
		}
	}

	@Override
	public void onClose() {
		super.onClose();
		ticky = 50;
	}
}
