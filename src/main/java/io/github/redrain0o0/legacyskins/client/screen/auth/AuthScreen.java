package io.github.redrain0o0.legacyskins.client.screen.auth;

import com.mojang.datafixers.util.Pair;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.modrinth.ModrinthOauth;
import io.github.redrain0o0.legacyskins.modrinth.ModrinthSkinPackCollection;
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class AuthScreen extends Screen {
	private Panel panel = Panel.centered(this, 300, 250);
	private final Screen parent;

	public AuthScreen(Screen parent) {
		super(Component.empty());
		this.parent = parent;
	}

	@Override
	public void onClose() {
		minecraft.setScreen(parent);
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
		panel = Panel.centered(this, ModrinthOauth.isAuthenticated() ? 300 : 170, ModrinthOauth.isAuthenticated() ? 250 : 40);
		panel.init();
		if (!ModrinthOauth.isAuthenticated()) {
			addRenderableWidget(Button.builder(Component.literal("Sign in with %sModrinth".formatted(ChatFormatting.GREEN)), b -> {
				signIntoModrinthAccount();
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + 10).build());
		} else {
			addRenderableWidget(Button.builder(Component.literal("Update skin packs"), b -> {
				updateSkinPacks();
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + 10).build());
			addRenderableWidget(Button.builder(Component.literal("Sign out of ").append(Component.literal("Modrinth").withStyle(ChatFormatting.GREEN)).append(" account").withStyle(ChatFormatting.RED), b -> {
				ModrinthOauth.unAuth();
				rebuildWidgets();
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + panel.height - 10 - 20).build());
		}
	}

	private void updateSkinPacks() {
		ModrinthSkinPackCollection.loadCollection().thenApply(f ->
				ModrinthSkinPackCollection.loadProjects(f).thenApply(g ->
						ModrinthSkinPackCollection.getVersionsOfProjects(g).thenApply(projectVersionsMap -> {
							HashMap<ModrinthDataObjects.Project, ModrinthDataObjects.Version> ilMap = new HashMap<>();
							for (Map.Entry<ModrinthDataObjects.Project, List<ModrinthDataObjects.Version>> projectListEntry : projectVersionsMap.entrySet()) {
								ModrinthDataObjects.Version latestVersion = getLatestVersion(projectListEntry.getValue());
								ilMap.put(projectListEntry.getKey(), latestVersion);
							}
							return ilMap;
						}).thenApply(data -> {
							Path resourcePackDirectory = minecraft.getResourcePackDirectory();
							int fileSize = 0;
							HashMap<ModrinthDataObjects.Project, ModrinthDataObjects.VersionFile> map = new HashMap<>();
							for (Map.Entry<ModrinthDataObjects.Project, ModrinthDataObjects.Version> projectVersionEntry : data.entrySet()) {
								ModrinthDataObjects.VersionFile versionFile = projectVersionEntry.getValue().files().stream().filter(ModrinthDataObjects.VersionFile::primary).findFirst().orElseThrow();
								fileSize += versionFile.size();
								map.put(projectVersionEntry.getKey(), versionFile);
							}
							System.out.println("Total size of download will be " + fileSize + " bytes");
							try {
								List<CompletableFuture<Pair<ModrinthDataObjects.VersionFile, Path>>> toDownload = new ArrayList<>();
								for (Map.Entry<ModrinthDataObjects.Project, ModrinthDataObjects.VersionFile> projectVersionFileEntry : map.entrySet()) {
									toDownload.add(CompletableFuture.supplyAsync(() -> {
										Path tempFile = null;
										try {
											tempFile = Files.createTempFile("legacyskins", ".zip");
										} catch (IOException e) {
											throw new RuntimeException(e);
										}
										return ModrinthSkinPackCollection.downloadFile(projectVersionFileEntry.getValue(), tempFile).join();
									}));
								}
								CompletableFuture.allOf(toDownload.toArray(CompletableFuture[]::new)).join();
								for (CompletableFuture<Pair<ModrinthDataObjects.VersionFile, Path>> pairCompletableFuture : toDownload) {
									Pair<ModrinthDataObjects.VersionFile, Path> join = pairCompletableFuture.join();
									Path path = resourcePackDirectory.resolve(join.getFirst().filename());
									try {
										Files.move(join.getSecond(), path);
									} catch (IOException e) {
										throw new RuntimeException(e);
									}
								}
								System.out.println("Downloaded all files");
							} catch (Throwable t) {
								Legacyskins.LOGGER.error("ERROR!", t);
							}
							return null;
						})
				)
		);

	}

	/*
	 * Sort by date published, and then pick the last one, it can be assumed that a project has at least 1 version.
	 */
	@SuppressWarnings("SequencedCollectionMethodCanBeUsed") // JAVA 17 does not have getLast()
	private ModrinthDataObjects.Version getLatestVersion(List<ModrinthDataObjects.Version> versions) {
		ArrayList<ModrinthDataObjects.Version> versions1 = new ArrayList<>(versions);
		versions1.sort(Comparator.comparing(ModrinthDataObjects.Version::datePublished));
		return versions1.get(versions1.size()-1);
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
