package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacyPackType;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.mixin.RenderableVListAccessor;
import io.github.redrain0o0.legacyskins.mixin.ScreenAccessor;
import io.github.redrain0o0.legacyskins.util.LegacySkinSprites;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import wily.legacy.Legacy4J;
import wily.legacy.client.ControlType;
import wily.legacy.client.controller.BindingState;
import wily.legacy.client.controller.Controller;
import wily.legacy.client.controller.ControllerBinding;
import wily.legacy.client.screen.ControlTooltip;
import wily.legacy.client.screen.LegacyScrollRenderer;
import wily.legacy.client.screen.Panel;
import wily.legacy.client.screen.PanelVListScreen;
import wily.legacy.client.screen.ScrollableRenderer;
import wily.legacy.init.LegacyRegistries;
import wily.legacy.util.LegacySprites;
import wily.legacy.util.ScreenUtil;

import java.util.*;
import java.util.function.Supplier;

import static wily.legacy.client.screen.ControlTooltip.COMPOUND_ICON_FUNCTION;

public class ChangeSkinScreen extends PanelVListScreen implements Controller.Event, ControlTooltip.Event {
	protected final Minecraft minecraft;
	protected final Panel tooltipBox = Panel.tooltipBoxOf(panel, 350);
	protected ScrollableRenderer scrollableRenderer = new ScrollableRenderer(new LegacyScrollRenderer());
	private Pair<ResourceLocation, LegacySkinPack> focusedPack;
	private PlayerSkinWidgetList playerSkinWidgetList;
	private final Map<ResourceLocation, Button> buttons = new HashMap<>();

	private boolean queuedChangeSkinPack = false;
	public ChangeSkinScreen(Screen parent) {
		super(parent, 180, 290, Component.empty());
		renderableVList.layoutSpacing(l -> 0);
		minecraft = Minecraft.getInstance();
		//int[] index = new int[]{0};
		LegacySkinPack.list.forEach((id, pack) -> {
			if (pack.type() == LegacyPackType.DEV && !Legacyskins.INSTANCE.showDevPacks() && !Legacyskins.INSTANCE.getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)).pack().equals(id)) return;
			Button button = new Button(0, 0, 260, 20, Component.translatable(Util.makeDescriptionId("skin_pack", id)), b -> {}, Supplier::get){
				@Override
				protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
					super.renderWidget(guiGraphics, i, j, f);
					if (this.isFocused()) {
						if (focusedPack != null && focusedPack.getSecond() == pack) return;
						ChangeSkinScreen.this.focusedPack = Pair.of(id, pack);
						queuedChangeSkinPack = true;
					}
				}

				@Override
				public boolean isHoveredOrFocused() {
					if (focusedPack != null && focusedPack.getSecond() == pack) return true;
					return super.isHoveredOrFocused();
				}
			};
			buttons.put(id, button);
			renderableVList.addRenderable(button);
		});
		openToCurrentSkin();
		// this.focusedPack = Pair.of(id, pack);
		//				skinPack();
//		for (LegacySkinPack legacySkinPack : LegacySkinPack.list.entrySet()) {
//			renderableVList.addRenderable(Button.builder(Component.translatable(legacySkinPack)))
//		}
//        list.forEach(s-> renderableVList.addRenderable(new AbstractButton(0,0,260,22, Component.translatable(s.buttonName().getString())) {
//            @Override
//            public void onPress() {
//                if (isFocused()) {
//					for (LegacySkinPack legacySkinPack : LegacySkinPack.list) {
//						System.out.println("Clicked");
//						Minecraft.getInstance().getToasts().addToast(new LegacyTip(Component.literal(legacySkinPack.skins().get(0).toString())));
//						LegacySkinUtils.switchSkin(legacySkinPack.skins().get(0));
//					}
//                }
//            }
//            @Override
//            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
//                defaultButtonNarrationText(narrationElementOutput);
//            }
//        }));
	}

	@Override
	public boolean keyPressed(int keyCode, int j, int k) {
		if (this.playerSkinWidgetList != null) this.setFocused(this.buttons.get(focusedPack.getFirst()));
		if (keyCode == InputConstants.KEY_RETURN) {
			if (this.playerSkinWidgetList != null) {
				Legacyskins.INSTANCE.setSkin(this.playerSkinWidgetList.element3.skinRef.get());
				ScreenUtil.playSimpleUISound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f);
			}
			return true;
		}
		if (keyCode == InputConstants.KEY_F) {
			favorite();
			return true;
		}
		if (control(keyCode == InputConstants.KEY_LBRACKET, keyCode == InputConstants.KEY_RBRACKET)) return true;
		if (control(keyCode == InputConstants.KEY_LEFT, keyCode == InputConstants.KEY_RIGHT)) return true;
		return super.keyPressed(keyCode,j,k);
	} // 91 93

	boolean control(boolean left, boolean right) {
		if ((left || right)) {
			if (this.playerSkinWidgetList != null) {
				if (this.playerSkinWidgetList.widgets.stream().anyMatch(a -> a.progress <= 1)) return true;
				int offset = 0;
				if (left) offset--;
				if (right) offset++;
				this.playerSkinWidgetList.sortForIndex(this.playerSkinWidgetList.index + offset);
				ScreenUtil.playSimpleUISound(LegacyRegistries.SCROLL.get(), 1.0f);
				return true;
			}
		}
		return false;
	}

	@Override
	public void bindingStateTick(BindingState state) {
		if (state.is(ControllerBinding.UP_BUTTON) && state.released) {
			favorite();
		}
		if (state.is(ControllerBinding.RIGHT_STICK) && state instanceof BindingState.Axis stick) {
			if (this.playerSkinWidgetList != null) {
				PlayerSkinWidget element3 = this.playerSkinWidgetList.element3;
				double sensitivity = 0.3d;
				element3.onDrag(0, 0, stick.getDeadZone() > Math.abs(stick.x) ? 0 : (double)stick.x * ((double)this.minecraft.getWindow().getScreenWidth() / (double)this.minecraft.getWindow().getGuiScaledWidth()) * ScreenUtil.getLegacyOptions().interfaceSensitivity().get() / 2.0 * sensitivity, stick.getDeadZone() > Math.abs(stick.y) ? 0 : (double)stick.y * ((double)this.minecraft.getWindow().getScreenWidth() / (double)this.minecraft.getWindow().getGuiScaledWidth()) * ScreenUtil.getLegacyOptions().interfaceSensitivity().get() / 2.0 * sensitivity);
			}
		}
	}

	private void favorite() {
		if (this.playerSkinWidgetList != null) {
			PlayerSkinWidget element3 = this.playerSkinWidgetList.element3;
			if (element3 != null) {
				SkinReference skinReference = element3.skinRef.get();
				ArrayList<SkinReference> favorites = Legacyskins.INSTANCE.getFavorites();
				if (favorites.contains(skinReference)) {
					favorites.removeIf(skinReference::equals);
				} else {
					favorites.add(skinReference);
				}
			}
			// this is definitely unfavorite
			if (Constants.FAVORITES_PACK.equals(this.focusedPack.getFirst())) {
				skinPack(playerSkinWidgetList.element4.skinRef.get().ordinal());
			}
			ScreenUtil.playSimpleUISound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f);
		}
	}

	@Override
	public void addControlTooltips(ControlTooltip.Renderer renderer) {
		super.addControlTooltips(renderer);
		renderer.set(0, () -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_RETURN) : ControllerBinding.DOWN_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.select_skin"));
		renderer.set(1, () -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_ESCAPE) : ControllerBinding.RIGHT_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.cancel"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.UP_BUTTON.bindingState.getIcon(), () -> Component.translatable(this.playerSkinWidgetList != null && Legacyskins.INSTANCE.getFavorites().contains(this.playerSkinWidgetList.element3.skinRef.get()) ? "legacyskins.menu.unfavorite" : "legacyskins.menu.favorite"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? COMPOUND_ICON_FUNCTION.apply(new ControlTooltip.Icon[]{ControlTooltip.getKeyIcon(InputConstants.KEY_LEFT),ControlTooltip.SPACE_ICON,ControlTooltip.getKeyIcon(InputConstants.KEY_RIGHT)})  : ControllerBinding.LEFT_STICK.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.navigate"));
		//renderer.add(()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.LEFT_STICK.bindingState.getIcon(), ()-> null);
	}

	@Override
	public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
		// Stop concurrent modification
		if (queuedChangeSkinPack) {
			queuedChangeSkinPack = false;
			skinPack();
		}
		ScreenUtil.renderDefaultBackground(guiGraphics, false);
		if (ScreenUtil.hasTooltipBoxes()) {
			guiGraphics.blitSprite(LegacySkinSprites.SKIN_PANEL, panel.x + panel.width - 10, panel.y + 7, tooltipBox.getWidth(), tooltipBox.getHeight() - 2);
			guiGraphics.blitSprite(LegacySkinSprites.PANEL_FILLER, panel.x + panel.width - 5, panel.y + 16 + tooltipBox.getHeight() - 80, tooltipBox.getWidth() - 14, 60);
			guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width - 1, panel.y + tooltipBox.getHeight() - 59, tooltipBox.getWidth() - 55, 55);
			guiGraphics.blit(VersionUtils.of(Legacy4J.MOD_ID,"textures/gui/sprites/container/sizeable_icon_holder.png"), panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 0, 0, 24, 24, 24, 24);
			guiGraphics.blit(VersionUtils.of(Legacy4J.MOD_ID,"textures/gui/sprites/container/sizeable_icon_holder.png"), panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 30, 0, 0, 24, 24, 24, 24);
			//guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 30, 24, 24);
			RenderSystem.enableBlend();
			guiGraphics.blitSprite(LegacySkinSprites.SKIN_BOX, panel.x + panel.width - 5, panel.y + 16, tooltipBox.getWidth() - 14, tooltipBox.getHeight() - 80);
			guiGraphics.blitSprite(LegacySkinSprites.PACK_NAME_BOX, panel.x + panel.width - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40);
			if (this.playerSkinWidgetList != null) {
				if (this.playerSkinWidgetList.element3.skinRef.get().equals(Legacyskins.INSTANCE.getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)))) {
					guiGraphics.blit(VersionUtils.of(Legacy4J.MOD_ID, "textures/gui/sprites/container/beacon_check.png"), panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 0, 0, 24, 24, 24, 24);
				}
				if (Legacyskins.INSTANCE.getFavorites().contains(this.playerSkinWidgetList.element3.skinRef.get())) {
					guiGraphics.blit(VersionUtils.ofMinecraft("textures/gui/sprites/hud/heart/container.png"), panel.x + panel.width + tooltipBox.getWidth() - 50 + 4, panel.y + tooltipBox.getHeight() - 60 + 30 + 4, 0, 0, 16, 16, 16, 16);
					guiGraphics.blit(VersionUtils.ofMinecraft("textures/gui/sprites/hud/heart/full.png"), panel.x + panel.width + tooltipBox.getWidth() - 50 + 4, panel.y + tooltipBox.getHeight() - 60 + 30 + 4, 0, 0, 16, 16, 16, 16);
				}

				{
					guiGraphics.pose().pushPose();
					// panel.x + panel.width - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
					int x = panel.x + panel.width - 5;
					int width = tooltipBox.getWidth() - 18;
					int middle = x + width / 2;
					guiGraphics.pose().translate(middle, panel.y + tooltipBox.getHeight() - 59 + 10, 0);
					guiGraphics.pose().scale(1.5f, 1.5f, 1);
					SkinReference reference = playerSkinWidgetList.element3.skinRef.get();
					ResourceLocation rl = reference.pack();
					guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("skin_pack.%s.%s".formatted(rl.toLanguageKey(), reference.ordinal())), 0, 0, 0xffffffff);
					guiGraphics.pose().popPose();
				}

				SkinReference reference = playerSkinWidgetList.element3.skinRef.get();
				ResourceLocation rl = reference.pack();
				if (I18n.exists("skin_pack.%s.%s.desc".formatted(rl.toLanguageKey(), reference.ordinal())))
				{
					guiGraphics.pose().pushPose();
					// panel.x + panel.width - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
					int x = panel.x + panel.width - 5;
					int width = tooltipBox.getWidth() - 18;
					int middle = x + width / 2;
					guiGraphics.pose().translate(middle, panel.y + tooltipBox.getHeight() - 59 + 35, 0);
					guiGraphics.pose().scale(1.5f, 1.5f, 1);
					guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("skin_pack.%s.%s.desc".formatted(rl.toLanguageKey(), reference.ordinal())), 0, 0, 0xffffffff);
					guiGraphics.pose().popPose();
				}
			}
			if (this.focusedPack != null) {
				{
					guiGraphics.pose().pushPose();
					// panel.x + panel.width - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
					int x = panel.x + panel.width - 5;
					int width = tooltipBox.getWidth() - 18;
					int middle = x + width / 2;
					guiGraphics.pose().translate(middle, panel.y + 16 + 4 + 7, 0);
					guiGraphics.pose().scale(1.5f, 1.5f, 1);
					guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable(Util.makeDescriptionId("skin_pack", focusedPack.getFirst())), 0, 0, 0xffffffff);
					guiGraphics.pose().popPose();
				}
				if (this.focusedPack.getSecond().type() != LegacyPackType.DEFAULT) {
					guiGraphics.pose().pushPose();
					// panel.x + panel.width - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
					int x = panel.x + panel.width - 5;
					int width = tooltipBox.getWidth() - 18;
					int middle = x + width / 2;
					guiGraphics.pose().translate(middle, panel.y + 16 + 4 + 25, 0);
					guiGraphics.pose().scale(1f, 1f, 1);
					guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable(this.focusedPack.getSecond().type().translationKey()), 0, 0, 0xffffffff);
					guiGraphics.pose().popPose();
				}
			}
			RenderSystem.disableBlend();

			//RenderSystem.enableScissor(panel.x + panel.width - 2, panel.y + 16, tooltipBox.getWidth() - 18, tooltipBox.getHeight() - 80);

			/*
			if (false) {
				LayerDefinition layerDefinition = LayerDefinitions.createRoots().get(ModelLayers.PLAYER);
				PlayerModel<LivingEntity> livingEntityPlayerModel = new PlayerModel<>(layerDefinition.bakeRoot(), false);
				RenderType renderType = livingEntityPlayerModel.renderType(VersionUtils.parse("minecraft:textures/entity/player/wide/steve.png"));
				guiGraphics.pose().pushPose();
				guiGraphics.pose().scale(100, 100, 100);
				guiGraphics.pose().scale(1, 1, -1);
				guiGraphics.pose().mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(180), 0, 0));
				guiGraphics.pose().mulPose((new Quaternionf()).rotationXYZ(0.43633232F, (float) Math.toRadians(System.currentTimeMillis() % 360), 3.1415927F));
				Lighting.setupForEntityInInventory();
				livingEntityPlayerModel.renderToBuffer(guiGraphics.pose(), guiGraphics.bufferSource().getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY/^, 1, 1, 1, 1^/);
				Lighting.setupFor3DItems();
				guiGraphics.pose().popPose();
			}
		    */
		}
	}

	@Override
	public boolean mouseScrolled(double d, double e, double f, double g) {
		if ((tooltipBox.isHovered(d, e) || !ControlType.getActiveType().isKbm()) && scrollableRenderer.mouseScrolled(g))
			return true;
		return super.mouseScrolled(d, e, f, g);
	}

	@Override
	public void renderableVListInit() {
		addRenderableOnly(((guiGraphics, i, j, f) -> guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + 7, panel.y + 7 + 130 - 8, panel.width - 14, panel.height - 14 - 135 + 1 + 8)));
		addRenderableOnly(((guiGraphics, i, j, f) -> guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + 34, panel.y + 10, 112, 112)));
		addRenderableOnly((guiGraphics, i, j, f) -> {
			if (this.focusedPack == null) return;
			ResourceLocation icon = this.focusedPack.getSecond().icon();
			// x, y, u, v, width, height, texWidth, texHeight?
			guiGraphics.blit(icon, panel.x + 35, panel.y + 11, 0, 0, 109, 109, 109, 109);
		});
		//addRenderableOnly(ChangeSkinScreen::renderDolls);

		//playerSkinWidgetList = PlayerSkinWidgetList.of(this.focusedPack.getSecond().skins().stream().map(a -> new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), () -> a))).toArray(PlayerSkinWidget[]::new));

		tooltipBox.init();
		getRenderableVList().init(this, panel.x + 11, panel.y + 11 + 125 - 10 + 5 - 15, panel.width - 22, panel.height - 135 + 10 - 2);
	}


	void openToCurrentSkin() {
		Optional<SkinReference> currentSkin = Legacyskins.INSTANCE.getCurrentSkin();
		SkinReference ref = currentSkin.orElse(new SkinReference(Constants.DEFAULT_PACK, 0));
		if (Legacyskins.INSTANCE.getFavorites().contains(ref)) {
			this.focusedPack = Pair.of(Constants.FAVORITES_PACK, LegacySkinPack.list.get(Constants.FAVORITES_PACK));
			this.queuedChangeSkinPack = true;
			ix();
			this.setFocused(this.buttons.get(focusedPack.getFirst()));
			skinPack(Legacyskins.INSTANCE.getFavorites().indexOf(ref));
			return;
		}
		if (currentSkin.isEmpty()) {
			// No skin
			this.focusedPack = Pair.of(Constants.DEFAULT_PACK, LegacySkinPack.list.get(Constants.DEFAULT_PACK));
			this.queuedChangeSkinPack = true;
			ix();
			this.setFocused(this.buttons.get(focusedPack.getFirst()));
			skinPack(0);
		} else {
			SkinReference skinReference = currentSkin.get();
			this.focusedPack = Pair.of(skinReference.pack(), LegacySkinPack.list.get(skinReference.pack()));
			this.queuedChangeSkinPack = true;
			ix();
			this.setFocused(this.buttons.get(focusedPack.getFirst()));
			skinPack(skinReference.ordinal());
		}
	}

	// TODO this assumes there's 6 skinpacks displayed.
	void ix() {
		int index = new ArrayList<>(LegacySkinPack.list.keySet()).indexOf(this.focusedPack.getFirst());
		if (index < 6) return;
		if (index > this.renderableVList.renderables.size() - 6) index = this.renderableVList.renderables.size() - 6;
		((RenderableVListAccessor) this.renderableVList).getScrolledList().set(index);
	}

	void skinPack() {
		if (this.focusedPack.getFirst().equals(Constants.FAVORITES_PACK) && Legacyskins.INSTANCE.getFavorites().contains(Legacyskins.INSTANCE.getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)))) {
			skinPack(Legacyskins.INSTANCE.getFavorites().indexOf(Legacyskins.INSTANCE.getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0))));
		} else if (Legacyskins.INSTANCE.getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)).pack().equals(this.focusedPack.getFirst())) {
			skinPack(Legacyskins.INSTANCE.getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)).ordinal());
		} else {
			skinPack(0);
		}
	}
	Renderable f;
	Renderable g;
	void skinPack(int index) {
		this.queuedChangeSkinPack = false;
		if (f != null) {
			((ScreenAccessor)this).getRenderables().remove(f);
		}
		if (g != null) {
			((ScreenAccessor)this).getRenderables().remove(g);
		}
		if (playerSkinWidgetList != null) {
			for (PlayerSkinWidget widget : playerSkinWidgetList.widgets) {
				removeWidget(widget);
			}
		}
		if (this.focusedPack != null) {
			int quota = 10;
			int x = (panel.x + panel.width);
			int y = (panel.y + 45);
			int width = (tooltipBox.getWidth() - 23);
			int height = tooltipBox.getHeight() - 80 - 50 + 40;
			// wedge the skins between 2 scissor renderables
			addRenderableOnly(f = (guiGraphics, i, j, f) -> {
				guiGraphics.enableScissor(x, y, x+width, y+height);
			});
			List<SkinReference> skins = new ArrayList<>();
			while (quota > 0) {
				int i = 0;
				if (Constants.FAVORITES_PACK.equals(this.focusedPack.getFirst())) {
					if (Legacyskins.INSTANCE.getFavorites().isEmpty()) break;
					for (SkinReference favorite : Legacyskins.INSTANCE.getFavorites()) {
						skins.add(favorite);
						i++;
						quota--;
					}
				} else {
					for (LegacySkin ignored : this.focusedPack.getSecond().skins()) {
						skins.add(new SkinReference(this.focusedPack.getFirst(), i));
						i++;
						quota--;
					}
				}
			}
			
			if (quota > 0) {
				playerSkinWidgetList = null;
			} else {
				playerSkinWidgetList = PlayerSkinWidgetList.of(x + width / 2 - 85 / 2, y + (height) / 2 - 120 / 2, skins.stream().map(a -> this.addRenderableWidget(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), () -> a))).toArray(PlayerSkinWidget[]::new));
				playerSkinWidgetList.sortForIndex(index);
			}
			addRenderableOnly(g = (guiGraphics, i, j, f) -> guiGraphics.disableScissor());
		}
	}

	private boolean firstOpen = false;

	@Override
	protected void init() {
		panel.height = Math.min(height, 290);
		super.init();
		panel.y = panel.y - 15;
		if (firstOpen) {
			firstOpen = false;
			openToCurrentSkin();
		} else {
			if (playerSkinWidgetList != null && playerSkinWidgetList.element3 != null && playerSkinWidgetList.element3.skinRef.get() != null) skinPack(playerSkinWidgetList.element3.skinRef.get().ordinal());
		}
	}
}