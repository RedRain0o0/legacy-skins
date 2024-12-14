package io.github.redrain0o0.legacyskins.client.screen.auth;

import io.github.redrain0o0.legacyskins.modrinth.ModrinthOauth;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.legacy.client.screen.LegacyLoadingScreen;

public class AuthScreen extends Screen {
	public AuthScreen() {
		super(Component.empty());
	}

	public void signIntoModrinthAccount() {
		LegacyLoadingScreen screen = new LegacyLoadingScreen(Component.literal("..."), Component.literal("..."));
		screen.genericLoading = true;
		assert minecraft != null;
		minecraft.setScreen(screen);
		ModrinthOauth.callbackInfo = (a, b) -> {
			screen.lastLoadingHeader = Component.literal(a + "");
			screen.lastLoadingStage = Component.literal(b);
			if (a == ModrinthOauth.Status.SERVER_CLOSED) minecraft.setScreen(this);
		};
	}

	@Override
	protected void init() {
		super.init();
		addRenderableWidget(Button.builder(Component.literal("Sign in with %sModrinth".formatted(ChatFormatting.GREEN)), b -> {
			signIntoModrinthAccount();
		}).width(100).pos(15, 15).build());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.render(guiGraphics, i, j, f);
		//guiGraphics.drawString(minecraft.font, "Logged in as [redacted].");
	}
}
