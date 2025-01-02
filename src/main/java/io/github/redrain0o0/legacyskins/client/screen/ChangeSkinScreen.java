package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacyPackType;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.screen.auth.AuthScreen;
import io.github.redrain0o0.legacyskins.client.util.Box2D;
import io.github.redrain0o0.legacyskins.client.util.EasterEggUtils;
import io.github.redrain0o0.legacyskins.client.util.SkinCollection;
import io.github.redrain0o0.legacyskins.mixin.legacy4j.RenderableVListAccessor;
import io.github.redrain0o0.legacyskins.mixin.ScreenAccessor;
import io.github.redrain0o0.legacyskins.util.GuiGraphicsUtils;
import io.github.redrain0o0.legacyskins.util.Legacy4JUtils;
import io.github.redrain0o0.legacyskins.util.LegacySkinSprites;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
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
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import org.slf4j.Logger;
import wily.legacy.Legacy4J;
import wily.legacy.client.ControlType;
//? if <1.20.2 {
/*//? if legacy4j: >=1.7.5 {
import wily.factoryapi.base.client.FactoryGuiGraphics;
//?} else {
/^import wily.legacy.client.LegacyGuiGraphics;
 ^///?}
*///?}
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

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

import static wily.legacy.client.screen.ControlTooltip.COMPOUND_ICON_FUNCTION;

// TODO classic change skin screen
public class ChangeSkinScreen extends PanelVListScreen implements Controller.Event, ControlTooltip.Event {
	protected final Minecraft minecraft;
	protected final Panel tooltipBox = Panel.tooltipBoxOf(panel, 350 + 50);
	protected ScrollableRenderer scrollableRenderer = new ScrollableRenderer(new LegacyScrollRenderer());
	private Pair<ResourceLocation, SkinCollection> focusedPack;
	private PlayerSkinWidgetList playerSkinWidgetList;
	private final Map<ResourceLocation, Button> buttons = new HashMap<>();
	private final boolean hardcoreModeHearts;

	private boolean queuedChangeSkinPack = false;
	public ChangeSkinScreen(Screen parent) {
		super(parent, 180, 290, Component.empty());
		hardcoreModeHearts = EasterEggUtils.isHardcoreMode();
		renderableVList.layoutSpacing(l -> 0);
		minecraft = Minecraft.getInstance();
		LegacySkinPack.list.forEach((id, pack) -> {
			if (pack.type() == LegacyPackType.DEV && !Legacyskins.INSTANCE.showDevPacks() && !Legacyskins.INSTANCE.getActiveSkinsConfig().getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)).pack().equals(id)) return;
			SkinCollection collection = SkinCollection.ofSkinPack(pack);
			Button button = new Button(0, 0, 260, 20, Component.translatable(Util.makeDescriptionId("skin_pack", id)), b -> {}, Supplier::get){
				@Override
				protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
					super.renderWidget(guiGraphics, i, j, f);
					if (this.isFocused()) {
						if (focusedPack != null && focusedPack.getSecond() == collection) return;
						ChangeSkinScreen.this.focusedPack = Pair.of(id, collection);
						queuedChangeSkinPack = true;
					}
				}

				@Override
				public boolean isHoveredOrFocused() {
					if (focusedPack != null && focusedPack.getSecond() == collection) return true;
					return super.isHoveredOrFocused();
				}
			};
			buttons.put(id, button);
			renderableVList.addRenderable(button);
		});
		openToCurrentSkin();
	}

	@Override
	public boolean keyPressed(int keyCode, int j, int k) {
		if (this.playerSkinWidgetList != null) this.setFocused(this.buttons.get(focusedPack.getFirst()));
		if (keyCode == InputConstants.KEY_RETURN) {
			selectSkin();
			return true;
		}
		if (keyCode == InputConstants.KEY_F) {
			favorite();
			return true;
		}
		if (keyCode == InputConstants.KEY_A) {
			minecraft.setScreen(new AuthScreen(this));
			return true;
		}
		if (keyCode == InputConstants.KEY_C && selectedSkinHasCreditsLink()) {
			openCreditsLink();
			return true;
		}
		if (control(keyCode == InputConstants.KEY_LBRACKET, keyCode == InputConstants.KEY_RBRACKET)) return true;
		if (control(keyCode == InputConstants.KEY_LEFT, keyCode == InputConstants.KEY_RIGHT)) return true;
		if (handleDollInteraction(keyCode == InputConstants.KEY_LSHIFT, keyCode == InputConstants.KEY_RSHIFT)) return true;
		return super.keyPressed(keyCode,j,k);
	} // 91 93

	private void openCreditsLink() {
		SkinReference skinReference = playerSkinWidgetList.element3.skinRef.get();

		LegacySkin legacySkin = LegacySkinPack.list.get(skinReference.pack()).skins().get(skinReference.ordinal());
		handleComponentClicked(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, legacySkin.creditsLink().get())));
	}

	private void selectSkin() {
		if (this.playerSkinWidgetList != null) {
			Legacyskins.INSTANCE.setSkin(this.playerSkinWidgetList.element3.skinRef.get());
			ScreenUtil.playSimpleUISound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f);
		}
	}

	@Override
	public boolean mouseClicked(double d, double e, int i) {
		// 	tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 0, 0, 24, 24, 24, 24
		//	tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 30, 0, 0, 24, 24, 24, 24
		Box2D selected = new Box2D(tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 24, 24);
		if (selected.isMouseInside(d, e)) {
			selectSkin();
			return true;
		}
		Box2D favorited = new Box2D(tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 30, 24, 24);
		if (favorited.isMouseInside(d, e)) {
			favorite();
			return true;
		}
		return super.mouseClicked(d, e, i);
	}

	boolean handleDollInteraction(boolean left, boolean right) {
		if (!(left || right)) return false;
		if (playerSkinWidgetList == null || playerSkinWidgetList.element3 == null || playerSkinWidgetList.element3.isInterpolating()) return false;
		if (left) {
			playerSkinWidgetList.element3.sktaeChange(PlayerSkinWidget.SLy.STEAKING);
		}
		if (right) {
			playerSkinWidgetList.element3.sktaeChange(PlayerSkinWidget.SLy.PRFINVING);
		}
		return true;
	}

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
		if (state.is(ControllerBinding.RIGHT_STICK_BUTTON) && state.justPressed) {
			if (handleDollInteraction(true, false)) return;
		}
		if (state.is(ControllerBinding.LEFT_BUTTON) && state.justPressed) {
			minecraft.setScreen(new AuthScreen(this));
			return;
		}
		if (state.is(ControllerBinding.START) && state.justPressed) {
			openCreditsLink();
			state.block();
			return;
		}
		if (state.is(ControllerBinding.RIGHT_STICK) && state instanceof BindingState.Axis stick) {
			if (this.playerSkinWidgetList != null) {
				PlayerSkinWidget element3 = this.playerSkinWidgetList.element3;
				double sensitivity = 0.3d;
				element3.onDrag(0, 0, stick.getDeadZone() > Math.abs(stick.x) ? 0 : (double)stick.x * ((double)this.minecraft.getWindow().getScreenWidth() / (double)this.minecraft.getWindow().getGuiScaledWidth()) * Legacy4JUtils.options().interfaceSensitivity() / 2.0 * sensitivity, stick.getDeadZone() > Math.abs(stick.y) ? 0 : (double)stick.y * ((double)this.minecraft.getWindow().getScreenWidth() / (double)this.minecraft.getWindow().getGuiScaledWidth()) * Legacy4JUtils.options().interfaceSensitivity() / 2.0 * sensitivity);
				state.block();
			}
		}
	}

	private void favorite() {
		if (this.playerSkinWidgetList != null) {
			PlayerSkinWidget element3 = this.playerSkinWidgetList.element3;
			if (element3 != null) {
				SkinReference skinReference = element3.skinRef.get();
				ArrayList<SkinReference> favorites = Legacyskins.INSTANCE.getActiveSkinsConfig().getFavorites();
				if (favorites.contains(skinReference)) {
					favorites.removeIf(skinReference::equals);
				} else {
					favorites.add(skinReference);
				}
			}
			// this is definitely unfavorite
			if (Constants.FAVORITES_PACK.equals(this.focusedPack.getFirst())) {
				skinPack(this.focusedPack.getSecond().indexOf(playerSkinWidgetList.element2.skinRef.get()));
			}
			ScreenUtil.playSimpleUISound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f);
		}
	}

	@Override
	public void addControlTooltips(ControlTooltip.Renderer renderer) {
		renderer.add(() -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_RETURN) : ControllerBinding.DOWN_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.select_skin"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_ESCAPE) : ControllerBinding.RIGHT_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.cancel"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.UP_BUTTON.bindingState.getIcon(), () -> Component.translatable(this.playerSkinWidgetList != null && Legacyskins.INSTANCE.getActiveSkinsConfig().getFavorites().contains(this.playerSkinWidgetList.element3.skinRef.get()) ? "legacyskins.menu.unfavorite" : "legacyskins.menu.favorite"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? COMPOUND_ICON_FUNCTION.apply(new ControlTooltip.Icon[]{ControlTooltip.getKeyIcon(InputConstants.KEY_LEFT),ControlTooltip.SPACE_ICON,ControlTooltip.getKeyIcon(InputConstants.KEY_RIGHT)})  : ControllerBinding.LEFT_STICK.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.navigate"));
		renderer.add(() -> selectedSkinHasCreditsLink() ? (ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_C) : ControllerBinding.START.bindingState.getIcon()) : (ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_A) : ControllerBinding.LEFT_BUTTON.bindingState.getIcon()), () -> selectedSkinHasCreditsLink() ? Component.literal("Show Credits") : Component.literal("Download Skin Packs"));
		//renderer.add(()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.LEFT_STICK.bindingState.getIcon(), ()-> null);
	}

	private boolean selectedSkinHasCreditsLink() {
		if (playerSkinWidgetList == null) return false;
		SkinReference skinReference = playerSkinWidgetList.element3.skinRef.get();

		LegacySkin legacySkin = LegacySkinPack.list.get(skinReference.pack()).skins().get(skinReference.ordinal());
		if (legacySkin == null) return false; // shouldn't happen
		return legacySkin.creditsLink().isPresent();
	}

	@Override
	public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
		// Stop concurrent modification
		if (queuedChangeSkinPack) {
			queuedChangeSkinPack = false;
			skinPack();
		}
		//? if legacy4j: <1.7.5 {
		/*ScreenUtil.renderDefaultBackground(guiGraphics, false);
		*///?} else
		ScreenUtil.renderDefaultBackground(wily.factoryapi.base.client.UIDefinition.Accessor.of(this), guiGraphics, false);
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySkinSprites.SKIN_PANEL, tooltipBox.x - 10, panel.y + 7, tooltipBox.getWidth(), tooltipBox.getHeight() - 2);
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySkinSprites.PANEL_FILLER, tooltipBox.x - 5, panel.y + 16 + tooltipBox.getHeight() - 80, tooltipBox.getWidth() - 14, 60);
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, tooltipBox.x - 1, panel.y + tooltipBox.getHeight() - 59, tooltipBox.getWidth() - 55, 55);
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(VersionUtils.of(Legacy4J.MOD_ID,"textures/gui/sprites/container/sizeable_icon_holder.png"), tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 0, 0, 24, 24, 24, 24);
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(VersionUtils.of(Legacy4J.MOD_ID,"textures/gui/sprites/container/sizeable_icon_holder.png"), tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 30, 0, 0, 24, 24, 24, 24);
		RenderSystem.enableBlend();
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySkinSprites.PACK_NAME_BOX, tooltipBox.x - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40);
		GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySkinSprites.SKIN_BOX, tooltipBox.x - 5, panel.y + 16, tooltipBox.getWidth() - 14, tooltipBox.getHeight() - 80);
		if (this.playerSkinWidgetList != null) {
			// Responsible for drawing the selected skin icon and its background
			if (this.playerSkinWidgetList.element3.skinRef.get().equals(Legacyskins.INSTANCE.getActiveSkinsConfig().getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0)))) {
				GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(VersionUtils.of(Legacy4J.MOD_ID, "textures/gui/sprites/container/beacon_check.png"), tooltipBox.x + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 0, 0, 24, 24, 24, 24);
			}

			// Responsible for drawing the favorites icon and its background
			if (Legacyskins.INSTANCE.getActiveSkinsConfig().getFavorites().contains(this.playerSkinWidgetList.element3.skinRef.get())) {
				//? if >=1.20.2 {
				GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(VersionUtils.ofMinecraft("textures/gui/sprites/hud/heart/container.png"), tooltipBox.x + tooltipBox.getWidth() - 50 + 4, panel.y + tooltipBox.getHeight() - 60 + 30 + 4, 0, 0, 16, 16, 16, 16);
				GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(VersionUtils.ofMinecraft("textures/gui/sprites/hud/heart/" + (hardcoreModeHearts ? "hardcore_" : "") + "full.png"), tooltipBox.x + tooltipBox.getWidth() - 50 + 4, panel.y + tooltipBox.getHeight() - 60 + 30 + 4, 0, 0, 16, 16, 16, 16);
				//?} else {
				/*// Method params
				// ResourceLocation atlasLocation, int x, int y, float uOffset, float vOffset, int width, int height, int textureWidth, int textureHeight
				// ResourceLocation atlasLocation, int x, int y, int width, int height, float uOffset, float vOffset, int uWidth, int vHeight, int textureWidth, int textureHeight
				guiGraphics.blit(VersionUtils.ofMinecraft("textures/gui/icons.png"), tooltipBox.x + tooltipBox.getWidth() - 50 + 4, panel.y + tooltipBox.getHeight() - 60 + 30 + 4, 16, 16, 16, 0, 9, 9, 256, 256);
				guiGraphics.blit(VersionUtils.ofMinecraft("textures/gui/icons.png"), tooltipBox.x + tooltipBox.getWidth() - 50 + 4, panel.y + tooltipBox.getHeight() - 60 + 30 + 4, 16, 16, 52, hardcoreModeHearts ? 45 : 0, 9, 9, 256, 256);
				*///?}
			}

			// Responsible for drawing the skin's name
			{
				guiGraphics.pose().pushPose();
				// tooltipBox.x - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
				int x = tooltipBox.x - 5;
				int width = tooltipBox.getWidth() - 18;
				int middle = x + width / 2;
				guiGraphics.pose().translate(middle, panel.y + tooltipBox.getHeight() - 59 + 10, 0);
				guiGraphics.pose().scale(1.5f, 1.5f, 1);
				SkinReference reference = playerSkinWidgetList.element3.skinRef.get();
				ResourceLocation rl = reference.pack();
				guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("skin_pack.%s.%s".formatted(rl.toLanguageKey(), reference.ordinal())), 0, 0, 0xffffffff);
				guiGraphics.pose().popPose();
			}

			// Responsible for drawing a skin's description
			SkinReference reference = playerSkinWidgetList.element3.skinRef.get();
			ResourceLocation rl = reference.pack();
			if (I18n.exists("skin_pack.%s.%s.desc".formatted(rl.toLanguageKey(), reference.ordinal())))
			{
				guiGraphics.pose().pushPose();
				// tooltipBox.x - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
				int x = tooltipBox.x - 5;
				int width = tooltipBox.getWidth() - 18;
				int middle = x + width / 2;
				guiGraphics.pose().translate(middle, panel.y + tooltipBox.getHeight() - 59 + 35, 0);
				guiGraphics.pose().scale(1.5f, 1.5f, 1);
				guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable("skin_pack.%s.%s.desc".formatted(rl.toLanguageKey(), reference.ordinal())), 0, 0, 0xffffffff);
				guiGraphics.pose().popPose();
			}
		}
		if (this.focusedPack != null) {
			// Responsible for drawing the skin pack's name
			{
				guiGraphics.pose().pushPose();
				// tooltipBox.x - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
				int x = tooltipBox.x - 5;
				int width = tooltipBox.getWidth() - 18;
				int middle = x + width / 2;
				guiGraphics.pose().translate(middle, panel.y + 16 + 4 + 7, 0);
				guiGraphics.pose().scale(1.5f, 1.5f, 1);
				guiGraphics.drawCenteredString(Minecraft.getInstance().font, Component.translatable(Util.makeDescriptionId("skin_pack", focusedPack.getFirst())), 0, 0, 0xffffffff);
				guiGraphics.pose().popPose();
			}

			// Responsible for displaying the Modrinth icon on packs downloaded by Modrinth
			{
				if (focusedPack != null && LegacySkinPack.modrinthSkinPacks.contains(focusedPack.getFirst())) {
					guiGraphics.pose().pushPose();
					int x = tooltipBox.x;
					int width = tooltipBox.getWidth() - 30;
					int wHLogo = 30;
					int placementX = x + width - wHLogo;
					guiGraphics.pose().translate(placementX, panel.y + 16 + 4 + 7 - 2, 0);
					GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(Constants.MODRINTH_STORE_LOGO, 0, 0, 0, 0, wHLogo, wHLogo, wHLogo, wHLogo);
					guiGraphics.pose().popPose();
				}
			}

			// Responsible for drawing the skin pack type
			if (this.focusedPack.getSecond().type() != LegacyPackType.DEFAULT) {
				guiGraphics.pose().pushPose();
				// tooltipBox.x - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40
				int x = tooltipBox.x - 5;
				int width = tooltipBox.getWidth() - 18;
				int middle = x + width / 2;
				guiGraphics.pose().translate(middle, panel.y + 16 + 4 + 25, 0);
				guiGraphics.pose().scale(1f, 1f, 1);
				SkinReference skinReference;
				Component text;
				if (Constants.CREDITORS_PACK.equals(this.focusedPack.getFirst()) && (skinReference = playerSkinWidgetList.element3.skinRef.get()) != null) {
					text = Component.translatable(Util.makeDescriptionId("skin_pack", skinReference.pack()));
				} else {
					text = Component.translatable(this.focusedPack.getSecond().type().translationKey());
				}
				guiGraphics.drawCenteredString(Minecraft.getInstance().font, text, 0, 0, 0xffffffff);
				guiGraphics.pose().popPose();
			}

			// Responsible for drawing the "BETA PACK!!! Things might break!" text when the Modern Defaults pack is shown
			if (this.focusedPack.getFirst().equals(Constants.MODERN_DEFAULTS_PACK)) {
				k++;
				PoseStack pose = guiGraphics.pose();
				pose.pushPose();
				pose.translate(width / 2f, height / 2f, 0);
				pose.mulPose(Axis.ZP.rotationDegrees(k));
				guiGraphics.drawCenteredString(minecraft.font, "BETA PACK!!! Things might break!", 0, 0, Color.HSBtoRGB((float) (Math.sin(k / 100f) + 1) / 2, 1, 1));
				pose.popPose();
			}
		}
		RenderSystem.disableBlend();
	}

	private int k;

	@Override
	public boolean mouseScrolled(double d, double e, /*? if >=1.20.2 {*/ double f, /*?}*/ double g) {
		if ((tooltipBox.isHovered(d, e) || !ControlType.getActiveType().isKbm()) && scrollableRenderer.mouseScrolled(g))
			return true;
		return super.mouseScrolled(d, e, /*? if >=1.20.2 {*/ f, /*?}*/ g);
	}

	private /*? if >=1.20.2 {*/ GuiGraphics /*?} else {*//*/^? if legacy4j: <1.7.5 {^//^LegacyGuiGraphics^//^?} else {^/FactoryGuiGraphics/^?}^/*//*?}*/ p(GuiGraphics in) {
		//? if >=1.20.2 {
		return in;
		 //?} else
		/*return*/ /*? if legacy4j: <1.7.5 {*//*LegacyGuiGraphics*//*?} else {*/FactoryGuiGraphics/*?}*/.of(in);
	}

	@Override
	public void renderableVListInit() {
		addRenderableOnly(((guiGraphics, i, j, f) -> GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + 7, panel.y + 7 + 130 - 8, panel.width - 14, panel.height - 14 - 135 + 1 + 8)));
		addRenderableOnly(((guiGraphics, i, j, f) -> GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + 34, panel.y + 10, 112, 112)));
		addRenderableOnly((guiGraphics, i, j, f) -> {
			if (this.focusedPack == null) return;
			ResourceLocation icon = EasterEggUtils.processIconId(this.focusedPack.getSecond().icon());
			// x, y, u, v, width, height, texWidth, texHeight?
			guiGraphics.pose().pushPose();
			guiGraphics.pose().translate(panel.x + 35.3, panel.y + 11.3, 0);
			GuiGraphicsUtils.ofGuiGraphics(guiGraphics).blit(icon, 0, 0, 0, 0, 109, 109, 109, 109);
			guiGraphics.pose().popPose();
		});

		tooltipBox.init("tooltipBox");
		// TODO, should the GUI api be able to touch this list?
		getRenderableVList().init(/*? if legacy4j: <1.7.5 {*//*this*//*?} else {*/"renderableVList"/*?}*/, panel.x + 11, panel.y + 11 + 125 - 10 + 5 - 15, panel.width - 22, panel.height - 135 + 10 - 2 /*? if legacy4j: >=1.7.5 {*/- 20/*?}*/);
	}


	void openToCurrentSkin() {
		Optional<SkinReference> currentSkin = Legacyskins.INSTANCE.getActiveSkinsConfig().getCurrentSkin();
		SkinReference ref = currentSkin.orElse(new SkinReference(Constants.DEFAULT_PACK, 0));
		{
			if (Legacyskins.INSTANCE.getActiveSkinsConfig().getFavorites().contains(ref)) {
				this.focusedPack = Pair.of(Constants.FAVORITES_PACK, SkinCollection.ofFavorites());
			} else if (SkinCollection.ofCreditors().has(ref)) {
				this.focusedPack = Pair.of(Constants.CREDITORS_PACK, SkinCollection.ofCreditors());
			} else {
				this.focusedPack = Pair.of(ref.pack(), SkinCollection.ofSkinPack(ref.pack()));
			}
			this.queuedChangeSkinPack = true;
			skinPack(this.focusedPack.getSecond().indexOf(ref));
			if (!firstOpen) ix();
			this.setFocused(this.buttons.get(focusedPack.getFirst()));
		}
	}

	@SuppressWarnings("LoggingSimilarMessage" /* Fix when it stops working */)
	void ix() {
		//? if <1.21.2 {
		/*ProfilerFiller profiler = this.minecraft.getProfiler();
		*///?} else {
		// TODO profiler
		var profiler = new Object() {
			void push(Supplier<String> supplier) {

			}
			void pop() {

			}
		};
		//?}
		Logger logger = Legacyskins.LOGGER;
		Renderable renderable = this.buttons.get(focusedPack.getFirst());
		for (Renderable renderable1 : ((ScreenAccessor) this).legacyskins$getRenderables()) {
			if (renderable1 instanceof Button button) {
				logger.debug("Button found: {}", button.getMessage().getString());
			}
		}
		if (renderable instanceof Button button) {
			if (this.children().contains(button) && button.visible) {
				logger.debug("Button was found and it is visible {} {}", button, button.getMessage().getString());
				// do nothing
			} else {
				profiler.push(() -> "Scrolling to " + button.getMessage());
				int i = 0;
				while (((RenderableVListAccessor)renderableVList).canScrollDown()) renderableVList.mouseScrolled(true);
				logger.debug("scrolled to the bottom");
				while(!this.children().contains(button)) {
					logger.debug("Searching for {}", button.getMessage());
					renderableVList.mouseScrolled(false);
					logger.debug("Scrolled up:");
					for (GuiEventListener renderable1 : this.children()) {
						if (renderable1 instanceof Button button2) {
							logger.debug("Button found: {}", button2.getMessage().getString());
						}
					}
					// Note, increase this if there people actually have more than 500 skin packs
					if (i++ > 500) {
						logger.error("Failed to find {}", button.getMessage().getString());
						logger.error("We have tried over 500 times and still haven't found the button");
						break;
					} else {
						logger.debug("Found {}", button.getMessage().getString());
					}
				}
				profiler.pop();
			}
		}
	}

	void skinPack() {
		SkinReference currentSkin = Legacyskins.INSTANCE.getActiveSkinsConfig().getCurrentSkin().orElse(new SkinReference(Constants.DEFAULT_PACK, 0));
		SkinCollection collection = this.focusedPack.getSecond();
		skinPack(collection.has(currentSkin) ? collection.indexOf(currentSkin) : 0);
	}
	Renderable f;
	Renderable g;
	void skinPack(int index) {
		this.queuedChangeSkinPack = false;
		// Clear up the 2 scissors, as well as all the dolls
		if (f != null) {
			((ScreenAccessor)this).legacyskins$getRenderables().remove(f);
		}
		if (g != null) {
			((ScreenAccessor)this).legacyskins$getRenderables().remove(g);
		}
		if (playerSkinWidgetList != null) {
			for (PlayerSkinWidget widget : playerSkinWidgetList.widgets) {
				removeWidget(widget);
			}
		}

		if (this.focusedPack != null) {
			int quota = 10;
			int x = (tooltipBox.x);
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
				if (this.focusedPack.getSecond().isEmpty()) break;
				for (SkinReference ref : this.focusedPack.getSecond().skins()) {
					skins.add(ref);
					quota--;
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

	private boolean firstOpen = true;

	@Override
	protected void init() {
		panel.height = Math.min(height, 297);
		super.init();
		panel.y = panel.y - 15;
		if (firstOpen) {
			firstOpen = false;
			Legacyskins.LOGGER.debug("Opened to current skin in init");
			openToCurrentSkin();
		} else {
			if (playerSkinWidgetList != null && playerSkinWidgetList.element3 != null && playerSkinWidgetList.element3.skinRef.get() != null) skinPack(playerSkinWidgetList.element3.skinRef.get().ordinal());
		}
	}
}