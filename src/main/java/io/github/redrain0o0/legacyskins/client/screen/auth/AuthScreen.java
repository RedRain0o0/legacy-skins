package io.github.redrain0o0.legacyskins.client.screen.auth;

import io.github.redrain0o0.legacyskins.modrinth.ModrinthOauth;
import io.github.redrain0o0.legacyskins.modrinth.data.ModrinthDataObjects;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.legacy.client.screen.LegacyLoadingScreen;
import wily.legacy.client.screen.ModsScreen;
import wily.legacy.client.screen.Panel;
import wily.legacy.util.LegacySprites;

public class AuthScreen extends Screen {
	private final Panel panel = Panel.centered(this, 300, 250);
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
			if (a == ModrinthOauth.Status.SERVER_CLOSED) {
				minecraft.tell(() -> minecraft.setScreen(this));
				ModrinthOauth.callbackInfo = (c, d) -> {}; // stop memory leak
			} else if (a == ModrinthOauth.Status.SERVER_STARTED) {
				Util.getPlatform().openUri(ModrinthOauth.OAUTH_URL);
			}
		};
		ModrinthOauth.enableOauthServer();
	}

	@Override
	protected void init() {
		super.init();
		panel.init();
		if (!ModrinthOauth.isAuthenticated()) {
			addRenderableWidget(Button.builder(Component.literal("Sign in with %sModrinth".formatted(ChatFormatting.GREEN)), b -> {
				signIntoModrinthAccount();
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + 10).build());
		} else {
			addRenderableWidget(Button.builder(Component.literal("Sign out of ").append(Component.literal("Modrinth").withStyle(ChatFormatting.GREEN)).append(" account").withStyle(ChatFormatting.RED), b -> {
				ModrinthOauth.unAuth();
				rebuildWidgets();
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + panel.height - 10 - 20).build());
		}
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
		super.renderBackground(guiGraphics, i, j, f);
		panel.render(guiGraphics, i, j, f);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.render(guiGraphics, i, j, f);
		if (ModrinthOauth.isAuthenticated()) {
			ModrinthOauth.ModrinthAuthentication.lazyLoad();
			if (ModrinthOauth.signedInUser.isDone()) {
				ModrinthDataObjects.User user = ModrinthOauth.signedInUser.join();
				guiGraphics.drawCenteredString(minecraft.font, Component.literal("Logged in as ").append(Component.literal(user.username()).withStyle(ChatFormatting.GOLD)).append("."), panel.x + panel.width / 2, panel.y + 10, 0xffffffff);
			}
		}
		//guiGraphics.drawString(minecraft.font, "Logged in as [redacted].");
	}
}
