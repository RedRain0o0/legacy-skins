package io.github.redrain0o0.legacyskins.client.screen.auth;

import com.mojang.datafixers.util.Pair;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.screen.ChangeSkinScreen;
import io.github.redrain0o0.legacyskins.modrinth.ModrinthOauth;
import io.github.redrain0o0.legacyskins.modrinth.ModrinthSkinPackCollection;
import io.github.redrain0o0.legacyskins.modrinth.data.ModrinthDataObjects;
import io.github.redrain0o0.legacyskins.util.TriConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import wily.legacy.client.LegacyTip;
import wily.legacy.client.screen.LegacyLoadingScreen;
import wily.legacy.client.screen.Panel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class AuthScreen extends Screen {
	private Panel panel = Panel.centered(this, 300, 250);
	private Screen parent;
	boolean replaceParent;

	public AuthScreen(Screen parent) {
		super(Component.empty());
		this.parent = parent;
	}

	@Override
	public void onClose() {
		if (replaceParent && parent instanceof ChangeSkinScreen screen) {
			System.out.println("changed parent screen");
			this.parent = new ChangeSkinScreen(screen.parent);
		}
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
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + 10 + 10).build());
			addRenderableWidget(Button.builder(Component.literal("Sign out of ").append(Component.literal("Modrinth").withStyle(ChatFormatting.GREEN)).append(" account").withStyle(ChatFormatting.RED), b -> {
				ModrinthOauth.unAuth();
				rebuildWidgets();
			}).width(150).pos(panel.x + panel.width / 2 - 150 / 2, panel.y + panel.height - 10 - 20).build());
		}
	}

	private LegacyTip tip = new LegacyTip(Component.literal("NKVFHKHF")).centered();
	private void updateSkinPacks() {

		LegacyLoadingScreen loadingScreen = new LegacyLoadingScreen(Component.literal("Downloading skin packs..."), Component.literal("...")) {
			@Override
			public LegacyTip getLoadingTip() {
				return tip;
			}
		};
		TriConsumer<String, String, Double> triConsumer = (a, b, c) -> {
			if (a != null) loadingScreen.lastLoadingHeader = Component.literal(a);
			if (b != null) loadingScreen.lastLoadingStage = Component.literal(b);
			if (c != null) loadingScreen.progress = (int) (c * 100);
		};
		Runnable finish = () -> minecraft.tell(() -> {
			this.replaceParent = true;
			minecraft.setScreen(this);
		});
		minecraft.setScreen(loadingScreen);
		triConsumer.accept(null, "Removing old skin packs...", null);
		CompletableFuture.runAsync(() -> {
			for (Map.Entry<ModrinthDataObjects.ProjectId, String> projectIdStringEntry : Legacyskins.lazyInstance().downloadedPacks().entrySet()) {
				try {
					Files.deleteIfExists(minecraft.getResourcePackDirectory().resolve(projectIdStringEntry.getValue()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			Legacyskins.lazyInstance().downloadedPacks().clear();
		}).thenRun(() -> {
			triConsumer.accept(null, "Loading collection...", null);
			ModrinthSkinPackCollection.loadCollection().thenApply(f -> {
						triConsumer.accept(null, "Loading projects...", null);
						return ModrinthSkinPackCollection.loadProjects(f).thenApply(g -> {
									triConsumer.accept(null, "Loading versions...", null);
									return ModrinthSkinPackCollection.getVersionsOfProjects(g).thenApply(projectVersionsMap -> {
										HashMap<ModrinthDataObjects.Project, ModrinthDataObjects.Version> ilMap = new HashMap<>();
										for (Map.Entry<ModrinthDataObjects.Project, List<ModrinthDataObjects.Version>> projectListEntry : projectVersionsMap.entrySet()) {
											ModrinthDataObjects.Version latestVersion = getLatestVersion(projectListEntry.getValue());
											ilMap.put(projectListEntry.getKey(), latestVersion);
										}
										return ilMap;
									}).thenApply(data -> {
										Path resourcePackDirectory = minecraft.getResourcePackDirectory();
										if (!resourcePackDirectory.toFile().exists()) resourcePackDirectory.toFile().mkdirs();
										int fileSize = 0;
										HashMap<ModrinthDataObjects.Project, ModrinthDataObjects.VersionFile> map = new HashMap<>();
										HashMap<ModrinthDataObjects.VersionFile, ModrinthDataObjects.Project> reversoMap = new HashMap<>();
										for (Map.Entry<ModrinthDataObjects.Project, ModrinthDataObjects.Version> projectVersionEntry : data.entrySet()) {
											ModrinthDataObjects.VersionFile versionFile = projectVersionEntry.getValue().files().stream().filter(ModrinthDataObjects.VersionFile::primary).findFirst().orElseThrow();
											fileSize += versionFile.size();
											map.put(projectVersionEntry.getKey(), versionFile);
											reversoMap.put(versionFile, projectVersionEntry.getKey());
										}
										String s = "Downloaded (%s/" + map.size() + ") resource packs";
										triConsumer.accept(null, s.formatted(0), 0d);
										System.out.println("Total size of download will be " + fileSize + " bytes");
										AtomicInteger downloaded = new AtomicInteger();
										int size = map.size();
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
													Pair<ModrinthDataObjects.VersionFile, Path> pair = ModrinthSkinPackCollection.downloadFile(projectVersionFileEntry.getValue(), tempFile).join();
													int i = downloaded.incrementAndGet();
													triConsumer.accept(null, s.formatted(i), ((i / (double) size)));
													return pair;
												}));
											}
											CompletableFuture.allOf(toDownload.toArray(CompletableFuture[]::new)).join();
											PackRepository resourcePackRepository = Minecraft.getInstance().getResourcePackRepository();
											triConsumer.accept(null, "Moving files to the resource packs folder...", null);
											for (CompletableFuture<Pair<ModrinthDataObjects.VersionFile, Path>> pairCompletableFuture : toDownload) {
												Pair<ModrinthDataObjects.VersionFile, Path> join = pairCompletableFuture.join();
												Path path = resourcePackDirectory.resolve(join.getFirst().filename());
												try {
													Files.move(join.getSecond(), path, StandardCopyOption.REPLACE_EXISTING);
													Legacyskins.lazyInstance().downloadedPacks().put(reversoMap.get(join.getFirst()).id(), join.getFirst().filename());
												} catch (IOException e) {
													throw new RuntimeException(e);
												}
											}
											triConsumer.accept(null, "Reloading resource packs...", null);
											resourcePackRepository.reload();
											Legacyskins.LOGGER.info(resourcePackRepository.getAvailablePacks().stream().map(Pack::getId).toList().toString());
											for (CompletableFuture<Pair<ModrinthDataObjects.VersionFile, Path>> pairCompletableFuture : toDownload) {
												Pair<ModrinthDataObjects.VersionFile, Path> join = pairCompletableFuture.join();
												resourcePackRepository.addPack("file/" + join.getFirst().filename());
											}
											System.out.println("Downloaded all files");
											minecraft.options.updateResourcePacks(resourcePackRepository);
											finish.run();
											//minecraft.tell(resourcePackRepository::reload);
										} catch (Throwable t) {
											Legacyskins.LOGGER.error("ERROR!", t);
										}
										return null;
									});
								}
						);
					}
			);
		});

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
	public void renderBackground(GuiGraphics guiGraphics /*? if >=1.20.2 {*/, int i, int j, float f/*?}*/) {
		super.renderBackground(guiGraphics /*? if >=1.20.2 {*/, i, j, f/*?}*/);
		//? if >=1.20.2
		panel.render(guiGraphics, i, j, f);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		//? if <1.20.2 {
		/*renderBackground(guiGraphics);
		panel.render(guiGraphics, i, j, f);
		*///?}
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
