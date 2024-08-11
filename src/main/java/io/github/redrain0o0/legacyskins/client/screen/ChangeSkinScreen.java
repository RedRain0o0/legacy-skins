package io.github.redrain0o0.legacyskins.client.screen;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.config.ConfigKeys;
import com.tom.cpm.shared.config.ModConfig;
import com.tom.cpm.shared.config.Player;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.util.LegacySkinSprites;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.LevelSummary;
import wily.legacy.Legacy4J;
import wily.legacy.Legacy4JClient;
import wily.legacy.Legacy4JPlatform;
import wily.legacy.client.ControlType;
import wily.legacy.client.LegacyTip;
import wily.legacy.client.LegacyWorldTemplate;
import wily.legacy.client.controller.Controller;
import wily.legacy.client.controller.ControllerBinding;
import wily.legacy.client.screen.*;
import wily.legacy.client.screen.compat.WorldHostFriendsScreen;
import wily.legacy.util.LegacySprites;
import wily.legacy.util.ModInfo;
import wily.legacy.util.ScreenUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import static wily.legacy.client.screen.CreationList.addIconButton;

public class ChangeSkinScreen extends PanelVListScreen implements Controller.Event,ControlTooltip.Event {
    protected final Map<ModInfo, SizedLocation> modLogosCache = new ConcurrentHashMap<>();
    protected ScrollableRenderer scrollableRenderer =  new ScrollableRenderer(new LegacyScrollRenderer());

    protected final Minecraft minecraft;

    public record SizedLocation(ResourceLocation location, int width, int height){
        public int getScaledWidth(int height){
            return (int) (height * ((float) width / height()));
        }
    }

    protected final Panel tooltipBox = Panel.tooltipBoxOf(panel,350);

    protected ModInfo focusedMod;
    protected final LoadingCache<ModInfo, List<FormattedCharSequence>> modLabelsCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
        @Override
        public List<FormattedCharSequence> load(ModInfo key) {
            List<Component> components = new ArrayList<>();
            SizedLocation logo = modLogosCache.get(key);
            if (logo != null && logo.getScaledWidth(28) >= 120){
                components.add(Component.literal(focusedMod.getName()));
                components.add(Component.translatable("legacy.menu.mods.id", focusedMod.getId()));
            }
            //if (!key.getAuthors().isEmpty())
            //    components.add(Component.translatable("legacy.menu.mods.authors", String.join(", ", key.getAuthors())));
            //if (!key.getCredits().isEmpty())
            //    components.add(Component.translatable("legacy.menu.mods.credits", String.join(", ", key.getCredits())));
            //key.getHomepage().ifPresent(s-> components.add(Component.translatable("legacy.menu.mods.homepage",s)));
            //key.getIssues().ifPresent(s-> components.add(Component.translatable("legacy.menu.mods.issues",s)));
            //key.getSources().ifPresent(s-> components.add(Component.translatable("legacy.menu.mods.sources",s)));
            //if (key.getLicense() != null && !key.getLicense().isEmpty()) components.add(Component.translatable("legacy.menu.mods.license", String.join(", ", key.getLicense())));
            //components.add(Component.literal(key.getDescription()));

            MultilineTooltip tooltip = new MultilineTooltip(components,tooltipBox.getWidth() - 16);
            return tooltip.toCharSequence(minecraft);
        }
    });

    public ChangeSkinScreen(Screen parent) {
        super(parent, 180, 290, Component.empty());
        renderableVList.layoutSpacing(l->0);
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
        LegacyWorldTemplate.list.forEach(s-> renderableVList.addRenderable(new AbstractButton(0,0,260,22, Component.translatable(s.buttonName().getString())) {
            @Override
            public void onPress() {
                if (isFocused()) {
                    //ModConfig.getCommonConfig().setString(ConfigKeys.SELECTED_MODEL,);
                    //ModConfig.getCommonConfig().setString(ConfigKeys.SELECTED_MODEL, ".minecraft/player_models/Model.cpmmodel");
                    //ModConfig.getCommonConfig().save();
                    //MinecraftClientAccess.get().sendSkinUpdate();
                }
            }
            @Override
            protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
                defaultButtonNarrationText(narrationElementOutput);
            }
        }));
    }

    @Override
    public void addControlTooltips(ControlTooltip.Renderer renderer) {
        super.addControlTooltips(renderer);
        renderer.set(0,()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_RETURN) : ControllerBinding.DOWN_BUTTON.bindingState.getIcon(), ()-> Component.translatable("legacyskins.menu.select_skin"));
        renderer.set(1,()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_ESCAPE) : ControllerBinding.RIGHT_BUTTON.bindingState.getIcon(), ()-> Component.translatable("legacyskins.menu.cancel"));
        //renderer.add(()-> ControlType.getActiveType().isKbm() ? ControlTooltip.getKeyIcon(InputConstants.KEY_F) : ControllerBinding.LEFT_STICK.bindingState.getIcon(), ()-> null);
    }

    @Override
    public void renderDefaultBackground(GuiGraphics guiGraphics, int i, int j, float f) {
        ScreenUtil.renderDefaultBackground(guiGraphics,false);
        if (ScreenUtil.hasTooltipBoxes()) {
            //tooltipBox.render(guiGraphics,i,j,f);
            guiGraphics.blitSprite(LegacySprites.SMALL_PANEL, panel.x + panel.width - 10, panel.y + 7, tooltipBox.getWidth(), tooltipBox.getHeight() - 2);
            guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width - 1, panel.y + tooltipBox.getHeight() - 60, tooltipBox.getWidth() - 55, 55);
            guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 2, 24, 24);
            guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + panel.width + tooltipBox.getWidth() - 50, panel.y + tooltipBox.getHeight() - 60 + 28, 24, 24);
            RenderSystem.enableBlend();
            guiGraphics.blitSprite(LegacySkinSprites.SKIN_BOX, panel.x + panel.width - 2, panel.y + 16, tooltipBox.getWidth() - 18, tooltipBox.getHeight() - 80); //12231`32
            RenderSystem.disableBlend();
            //guiGraphics.

            if (focusedMod != null) {
                List<FormattedCharSequence> label = modLabelsCache.getUnchecked(focusedMod);
                scrollableRenderer.scrolled.max = Math.max(0,label.size() - (tooltipBox.getHeight() - 50) / 12);
                SizedLocation logo = modLogosCache.get(focusedMod);
                int x = panel.x + panel.width + (logo == null ? 5 : logo.getScaledWidth(28) + 10);
                if (logo != null)
                    guiGraphics.blit(logo.location, panel.x + panel.width - 5, panel.y + 0, 0.0f, 0.0f, logo.getScaledWidth(28), 28, logo.getScaledWidth(28), 28);
                    //guiGraphics.pose(logo.location, panel.x + panel.width - 5, panel.y + 0, 0.0f, 0.0f, logo.getScaledWidth(28), 28, logo.getScaledWidth(28), 28);
                if (logo == null || logo.getScaledWidth(28) < 120) {
                    //ScreenUtil.renderScrollingString(guiGraphics, font, Component.translatable("legacy.menu.mods.id", focusedMod.getId()), x, panel.y + 12, panel.x + panel.width + 185, panel.y + 24, 0xFFFFFF, true);
                    //ScreenUtil.renderScrollingString(guiGraphics, font, Component.translatable("legacy.menu.mods.version",focusedMod.getVersion()), x, panel.y + 24, panel.x + panel.width + 185, panel.y + 36, 0xFFFFFF, true);
                }
                scrollableRenderer.render(guiGraphics, panel.x + panel.width + 5, panel.y + 38, tooltipBox.getWidth() - 16, tooltipBox.getHeight() - 50, () -> label.forEach(c->guiGraphics.drawString(font, c,panel.x + panel.width + 5, panel.y + 41 + label.indexOf(c) * 12, 0xFFFFFF)));
            }
        }
    }

    @Override
    public boolean mouseScrolled(double d, double e, double f, double g) {
        if ((tooltipBox.isHovered(d,e) || !ControlType.getActiveType().isKbm()) && scrollableRenderer.mouseScrolled(g)) return true;
        return super.mouseScrolled(d, e, f, g);
    }

    @Override
    public void renderableVListInit() {
        addRenderableOnly(((guiGraphics, i, j, f) -> guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + 7, panel.y + 7 + 130 - 8, panel.width - 14, panel.height - 14 - 135 + 1 + 8)));
        addRenderableOnly(((guiGraphics, i, j, f) -> guiGraphics.blitSprite(LegacySprites.SQUARE_RECESSED_PANEL, panel.x + 34, panel.y + 10, 112, 112)));

        tooltipBox.init();
        getRenderableVList().init(this,panel.x + 11,panel.y + 11 + 125 - 10 + 5 - 15,panel.width - 22, panel.height - 135 + 10 - 2);
    }

    //@Override public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float tickDelta) {
    //    super.render();
    //    if (focusedMod != null) {
    //        SizedLocation logo = modLogosCache.get(focusedMod);
    //        if (logo != null)
    //            guiGraphics.blit(logo.location, panel.x + panel.width - 5, panel.y + 0, 0.0f, 0.0f, logo.getScaledWidth(28), 28, logo.getScaledWidth(28), 28);
    //    }
    //}

    @Override
    protected void init() {
        panel.height = Math.min(height, 290);
        super.init();
        panel.y = panel.y - 15;
    }
}