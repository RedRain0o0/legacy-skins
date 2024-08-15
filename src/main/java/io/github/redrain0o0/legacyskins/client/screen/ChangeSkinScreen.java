package io.github.redrain0o0.legacyskins.client.screen;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.util.LegacySkinSprites;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Quaternionf;
import wily.legacy.client.ControlType;
import wily.legacy.client.controller.Controller;
import wily.legacy.client.controller.ControllerBinding;
import wily.legacy.client.screen.*;
import wily.legacy.util.LegacySprites;
import wily.legacy.util.ModInfo;
import wily.legacy.util.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChangeSkinScreen extends PanelVListScreen implements Controller.Event, ControlTooltip.Event {
	protected final Map<ModInfo, SizedLocation> modLogosCache = new ConcurrentHashMap<>();
	protected final Minecraft minecraft;
	protected final Panel tooltipBox = Panel.tooltipBoxOf(panel, 350);
	protected ScrollableRenderer scrollableRenderer = new ScrollableRenderer(new LegacyScrollRenderer());
	protected ModInfo focusedMod;
	protected final LoadingCache<ModInfo, List<FormattedCharSequence>> modLabelsCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
		@Override
		public List<FormattedCharSequence> load(ModInfo key) {
			List<Component> components = new ArrayList<>();
			SizedLocation logo = modLogosCache.get(key);
			if (logo != null && logo.getScaledWidth(28) >= 120) {
				components.add(Component.literal(focusedMod.getName()));
				components.add(Component.translatable("legacy.menu.mods.id", focusedMod.getId()));
			}

			MultilineTooltip tooltip = new MultilineTooltip(components, tooltipBox.getWidth() - 16);
			return tooltip.toCharSequence(minecraft);
		}
	});
	private Pair<ResourceLocation, LegacySkinPack> focusedPack;
	public ChangeSkinScreen(Screen parent) {
		super(parent, 180, 290, Component.empty());
		renderableVList.layoutSpacing(l -> 0);
		//if (focusedMod != null) {
		//    SizedLocation logo = modLogosCache.get(focusedMod);
		//    if (logo != null)
		//        addRenderableOnly(((guiGraphics, i, j, f) -> guiGraphics.blit(logo.location, panel.x + panel.width - 5, panel.y + 0, 0.0f, 0.0f, logo.getScaledWidth(28), 28, logo.getScaledWidth(28), 28)));
		//}
		//Legacy4JPlatform.getMods().forEach(mod->{
		//    if (mod.isHidden()) return;
		//    renderableVList.addRenderable(new AbstractButton(0,0,260,22, Component.literal(mod.getName())) {
		//        @Override
		//        public void onPress() {
		//            if (isFocused()){
//
		//            }
		//        }
//
		//        @Override
		//        protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
		//            super.renderWidget(guiGraphics, i, j, f);
		//            if (isFocused()) focusedMod = mod;
		//            RenderSystem.enableBlend();
		//            SizedLocation logo = modLogosCache.computeIfAbsent(mod, m-> {
		//                Optional<String> opt = m.getLogoFile(100);
		//                if (opt.isPresent() && mod.findResource(opt.get()).isPresent())
		//                    try {
		//                        NativeImage image = NativeImage.read(Files.newInputStream(mod.findResource(opt.get()).get()));
		//                        return new SizedLocation(minecraft.getTextureManager().register(opt.get().toLowerCase(Locale.ENGLISH), new DynamicTexture(image)),image.getWidth(),image.getHeight());
		//                    } catch (IOException e) {
		//                    }
		//                ResourceLocation defaultLogo = PackSelector.DEFAULT_ICON;
		//                if (mod.getId().equals("minecraft")) defaultLogo = PackSelector.loadPackIcon(minecraft.getTextureManager(),minecraft.getResourcePackRepository().getPack("vanilla"),"pack.png",defaultLogo);
		//                return new SizedLocation(defaultLogo,1,1);
		//            });
		//            //if (logo != null) guiGraphics.blit(logo.location,getX() + 5, getY() + 5, 0,0, logo.getScaledWidth(20),20,logo.getScaledWidth(20),20);
//
		//            RenderSystem.disableBlend();
		//        }
		//        @Override
		//        protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		//            defaultButtonNarrationText(narrationElementOutput);
		//        }
		//    });
		//});
		minecraft = Minecraft.getInstance();
		LegacySkinPack.list.forEach((id, pack) -> {
			renderableVList.addRenderable(Button.builder(Component.translatable(Util.makeDescriptionId("skin_pack", id)), button -> {
				this.focusedPack = Pair.of(id, pack);
			}).width(260).build());
		});
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

	private static void renderDolls(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {

	}

	@Override
	public void addControlTooltips(ControlTooltip.Renderer renderer) {
		super.addControlTooltips(renderer);
		renderer.set(0, () -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_RETURN) : ControllerBinding.DOWN_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.select_skin"));
		renderer.set(1, () -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_ESCAPE) : ControllerBinding.RIGHT_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.cancel"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.UP_BUTTON.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.favourite"));
		renderer.add(() -> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_B) : ControllerBinding.LEFT_STICK.bindingState.getIcon(), () -> Component.translatable("legacyskins.menu.navigate"));
		//renderer.add(()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.LEFT_STICK.bindingState.getIcon(), ()-> null);
	}

	@Override
	public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
		ScreenUtil.renderDefaultBackground(guiGraphics, false);
		if (ScreenUtil.hasTooltipBoxes()) {
			guiGraphics.blitSprite(LegacySkinSprites.SKIN_PANEL, panel.x + panel.width - 10, panel.y + 7, tooltipBox.getWidth(), tooltipBox.getHeight() - 2);
			guiGraphics.blitSprite(LegacySkinSprites.PANEL_FILLER, panel.x + panel.width - 5, panel.y + 16 + tooltipBox.getHeight() - 80, tooltipBox.getWidth() - 14, 60);
			guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width - 1, panel.y + tooltipBox.getHeight() - 59, tooltipBox.getWidth() - 55, 55);
			guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 3, 24, 24);
			guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 30, 24, 24);
			RenderSystem.enableBlend();
			guiGraphics.blitSprite(LegacySkinSprites.SKIN_BOX, panel.x + panel.width - 5, panel.y + 16, tooltipBox.getWidth() - 14, tooltipBox.getHeight() - 80);
			guiGraphics.blitSprite(LegacySkinSprites.PACK_NAME_BOX, panel.x + panel.width - 5, panel.y + 16 + 4, tooltipBox.getWidth() - 18, 40);
			RenderSystem.disableBlend();

			//RenderSystem.enableScissor(panel.x + panel.width - 2, panel.y + 16, tooltipBox.getWidth() - 18, tooltipBox.getHeight() - 80);

			LayerDefinition layerDefinition = LayerDefinitions.createRoots().get(ModelLayers.PLAYER);
			PlayerModel<LivingEntity> livingEntityPlayerModel = new PlayerModel<>(layerDefinition.bakeRoot(), false);
			RenderType renderType = livingEntityPlayerModel.renderType(ResourceLocation.parse("minecraft:textures/entity/player/wide/steve.png"));
			guiGraphics.pose().pushPose();
			guiGraphics.pose().scale(100, 100, 100);
			guiGraphics.pose().scale(1, 1, -1);
			guiGraphics.pose().mulPose(new Quaternionf().rotationXYZ((float) Math.toRadians(180), 0, 0));
			guiGraphics.pose().mulPose((new Quaternionf()).rotationXYZ(0.43633232F, (float) Math.toRadians(System.currentTimeMillis() % 360), 3.1415927F));
			Lighting.setupForEntityInInventory();
			livingEntityPlayerModel.renderToBuffer(guiGraphics.pose(), guiGraphics.bufferSource().getBuffer(renderType), 0xf000f0, OverlayTexture.NO_OVERLAY/*, 1, 1, 1, 1*/);
			Lighting.setupFor3DItems();
			guiGraphics.pose().popPose();

			//RenderSystem.disableScissor();

			if (focusedMod != null) {
				List<FormattedCharSequence> label = modLabelsCache.getUnchecked(focusedMod);
				scrollableRenderer.scrolled.max = Math.max(0, label.size() - (tooltipBox.getHeight() - 50) / 12);
				SizedLocation logo = modLogosCache.get(focusedMod);
				int x = panel.x + panel.width + (logo == null ? 5 : logo.getScaledWidth(28) + 10);
				if (logo != null)
					guiGraphics.blit(logo.location, panel.x + panel.width - 5, panel.y, 0.0f, 0.0f, logo.getScaledWidth(28), 28, logo.getScaledWidth(28), 28);
				if (logo == null || logo.getScaledWidth(28) < 120) {
					//ScreenUtil.renderScrollingString(guiGraphics, font, Component.translatable("legacy.menu.mods.id", focusedMod.getId()), x, panel.y + 12, panel.x + panel.width + 185, panel.y + 24, 0xFFFFFF, true);
					//ScreenUtil.renderScrollingString(guiGraphics, font, Component.translatable("legacy.menu.mods.version",focusedMod.getVersion()), x, panel.y + 24, panel.x + panel.width + 185, panel.y + 36, 0xFFFFFF, true);
				}
				scrollableRenderer.render(guiGraphics, panel.x + panel.width + 5, panel.y + 38, tooltipBox.getWidth() - 16, tooltipBox.getHeight() - 50, () -> label.forEach(c -> guiGraphics.drawString(font, c, panel.x + panel.width + 5, panel.y + 41 + label.indexOf(c) * 12, 0xFFFFFF)));
			}
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
		addRenderableOnly(ChangeSkinScreen::renderDolls);
		addRenderableWidget(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), () -> new LegacySkin(ResourceLocation.parse("legacyskins:b.cpmmodel"))));

		tooltipBox.init();
		getRenderableVList().init(this, panel.x + 11, panel.y + 11 + 125 - 10 + 5 - 15, panel.width - 22, panel.height - 135 + 10 - 2);
	}

	@Override
	protected void init() {
		panel.height = Math.min(height, 290);
		super.init();
		panel.y = panel.y - 15;
	}

	//@Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
	//    super.render();
	//    if (focusedMod != null) {
	//        SizedLocation logo = modLogosCache.get(focusedMod);
	//        if (logo != null)
	//            guiGraphics.blit(logo.location, panel.x + panel.width - 5, panel.y + 0, 0.0f, 0.0f, logo.getScaledWidth(28), 28, logo.getScaledWidth(28), 28);
	//    }
	//}

//	// TODO whose left and whose right?
//	// translate the pose before calling renderDoll
//	// pos < 0 -> faces left
//	// pos = 0 -> faces forwards
//	// pos > 0 -> faces right
//	public void renderDoll(GuiGraphics graphics, double pos, LegacySkin skin) {
//		//CPMCompat.createRenderer().setRenderModel();
//		CPMCompat.createRenderer().setRenderModel(new PlayerModel<>());
//	}

	public record SizedLocation(ResourceLocation location, int width, int height) {
		public int getScaledWidth(int height) {
			return (int) (height * ((float) width / height()));
		}
	}
}