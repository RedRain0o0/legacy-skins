package io.github.redrain0o0.legacyskins.client.screen.auth;

import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.client.screen.ChangeSkinScreen;
import io.github.redrain0o0.legacyskins.mixin.legacy4j.LegacyTipAccessor;
import io.github.redrain0o0.legacyskins.modrinth.ModrinthOauth;
import io.github.redrain0o0.legacyskins.modrinth.ModrinthSkinPackCollection;
import io.github.redrain0o0.legacyskins.modrinth.data.ModrinthDataObjects;
import io.github.redrain0o0.legacyskins.util.Legacy4JUtils;
import io.github.redrain0o0.legacyskins.util.TriConsumer;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import wily.legacy.client.GlobalPacks;
import wily.legacy.client.LegacyTip;
import wily.legacy.client.screen.LegacyLoadingScreen;
import wily.legacy.client.screen.Panel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

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
			this.parent = new ChangeSkinScreen(screen.parent);
		}
		minecraft.setScreen(parent);
	}

	public void signIntoModrinthAccount() {
		LegacyLoadingScreen screen = new LegacyLoadingScreen(Component.literal("..."), Component.literal("..."));
		Legacy4JUtils.LegacyLoadingScreenInterface screenInterface = Legacy4JUtils.loadingScreen(screen);
		screenInterface.setGenericLoading(true);
		assert minecraft != null;
		minecraft.setScreen(screen);
		ModrinthOauth.callbackInfo = (a, b) -> {
			screenInterface.setLoadingHeader(Component.literal(a + ""));
			screenInterface.setLoadingStage(Component.literal(b));
			if (a == ModrinthOauth.Status.SERVER_CLOSED) {
				VersionUtils.schedule(() -> minecraft.setScreen(this));
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

	private LegacyTip tip = ((Supplier<LegacyTip>) () -> {
		LegacyTip replace = new LegacyTip(Component.empty()).centered();
		replace.width = 300;
		return replace;
	}).get();
	private void updateSkinPacks() {
		Deque<ModrinthSkinPackCollection.DownloadProgressInfo> infoStack = new ArrayDeque<>();
		LegacyLoadingScreen loadingScreen = new LegacyLoadingScreen(Component.literal("Downloading skin packs..."), Component.literal("...")) {
			@SuppressWarnings("SequencedCollectionMethodCanBeUsed")
			@Override
			public LegacyTip getLoadingTip() {
				ArrayList<Component> components = new ArrayList<>();
				for (ModrinthSkinPackCollection.DownloadProgressInfo downloadProgressInfo : infoStack) {
					components.add(downloadProgressInfo.format(20));
				}
				List<Component> list = new ArrayList<>(components.stream().filter(a -> a.getStyle().getColor() != null && a.getStyle().getColor().getValue() == ChatFormatting.GREEN.getColor()).toList());
				while (components.size() > 10) {
					if (list.isEmpty()) break;
					components.remove(list.remove(0));
				}
				MutableComponent component = Component.empty();
				for (Component component1 : components) {
					component = component.append(component1).append("\n");
				}
				tip.tip(component);
				tip.width = Math.max(this.width - 30, 300);
				int tipLabelHeight = ((LegacyTipAccessor) tip).getTipLabel().getHeight();
				tip.height = tipLabelHeight <= 0 ? 0 : tipLabelHeight + 13;
				return tip;
			}
		};
		Legacy4JUtils.LegacyLoadingScreenInterface screenInterface = Legacy4JUtils.loadingScreen(loadingScreen);
		TriConsumer<String, String, Double> triConsumer = (a, b, c) -> {
			if (a != null) screenInterface.setLoadingHeader(Component.literal(a));
			if (b != null) screenInterface.setLoadingStage(Component.literal(b));
			if (c != null) screenInterface.setProgress((int) (c * 100));
		};
		Runnable finish = () -> VersionUtils.schedule(() -> {
			this.replaceParent = true;
			minecraft.setScreen(this);
		});
		minecraft.setScreen(loadingScreen);
		triConsumer.accept(null, "Removing old skin packs...", null);
		CompletableFuture.runAsync(() -> {
			for (Map.Entry<ModrinthDataObjects.ProjectId, String> projectIdStringEntry : LegacySkins.lazyInstance().downloadedPacks().entrySet()) {
				try {
					Files.deleteIfExists(minecraft.getResourcePackDirectory().resolve(projectIdStringEntry.getValue()));
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			LegacySkins.lazyInstance().downloadedPacks().clear();
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
											List<CompletableFuture<ModrinthSkinPackCollection.DownloadedFile>> toDownload = new ArrayList<>();
											for (Map.Entry<ModrinthDataObjects.Project, ModrinthDataObjects.VersionFile> projectVersionFileEntry : map.entrySet()) {
												toDownload.add(CompletableFuture.supplyAsync(() -> {
													Path tempFile = null;
													try {
														tempFile = Files.createTempFile("legacyskins", ".zip");
													} catch (IOException e) {
														throw new RuntimeException(e);
													}
													ModrinthSkinPackCollection.DownloadProgressInfo downloadProgressInfo = new ModrinthSkinPackCollection.DownloadProgressInfo();
													downloadProgressInfo.totalBytes = projectVersionFileEntry.getValue().size();
													downloadProgressInfo.hasTotalBytes = true;
													downloadProgressInfo.done = infoStack::removeFirstOccurrence;
													downloadProgressInfo.packName = projectVersionFileEntry.getKey().name();
													infoStack.addFirst(downloadProgressInfo);
													ModrinthSkinPackCollection.DownloadedFile downloadedFile = ModrinthSkinPackCollection.downloadFile(new ModrinthSkinPackCollection.DownloadInfo(projectVersionFileEntry.getValue(), tempFile), downloadProgressInfo).join();
													int i = downloaded.incrementAndGet();
													triConsumer.accept(null, s.formatted(i), ((i / (double) size)));
													return downloadedFile;
												}));
											}
											CompletableFuture.allOf(toDownload.toArray(CompletableFuture[]::new)).join();
											PackRepository resourcePackRepository = Minecraft.getInstance().getResourcePackRepository();
											triConsumer.accept(null, "Moving files to the resource packs folder...", null);
											for (CompletableFuture<ModrinthSkinPackCollection.DownloadedFile> pairCompletableFuture : toDownload) {
												ModrinthSkinPackCollection.DownloadedFile join = pairCompletableFuture.join();
												Path path = resourcePackDirectory.resolve(join.mrMetadata().filename());
												try {
													Files.move(join.realLocation(), path, StandardCopyOption.REPLACE_EXISTING);
													LegacySkins.lazyInstance().downloadedPacks().put(reversoMap.get(join.mrMetadata()).id(), join.mrMetadata().filename());
												} catch (IOException e) {
													throw new RuntimeException(e);
												}
											}
											triConsumer.accept(null, "Reloading resource packs...", null);
											resourcePackRepository.reload();
											LegacySkins.LOGGER.info(resourcePackRepository.getAvailablePacks().stream().map(Pack::getId).toList().toString());
											GlobalPacks globalPacks = GlobalPacks.globalResources.get();
											boolean b = globalPacks.applyOnTop();
											List<String> list = new ArrayList<>(globalPacks.list());
											for (CompletableFuture<ModrinthSkinPackCollection.DownloadedFile> pairCompletableFuture : toDownload) {
												ModrinthSkinPackCollection.DownloadedFile join = pairCompletableFuture.join();
												resourcePackRepository.addPack("file/" + join.mrMetadata().filename());
												if (!list.contains("file/" + join.mrMetadata().filename())) {
													list.add("file/" + join.mrMetadata().filename());
												}
											}
											System.out.println("Downloaded all files");
											GlobalPacks.globalResources.set(new GlobalPacks(list, b));
											minecraft.options.updateResourcePacks(resourcePackRepository);
											finish.run();
											//minecraft.tell(resourcePackRepository::reload);
										} catch (Throwable t) {
											LegacySkins.LOGGER.error("ERROR!", t);
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
