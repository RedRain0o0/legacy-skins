//? if figurac {
package io.github.redrain0o0.legacyskins.client.screen;

import io.github.redrain0o0.legacyskins.util.CommonMatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.figuramc.figura.config.Configs;
import org.figuramc.figura.gui.screens.AbstractPanelScreen;
import org.figuramc.figura.gui.screens.ConfigScreen;
import org.figuramc.figura.gui.screens.WardrobeScreen;
import org.figuramc.figura.utils.ui.UIHelper;
import wily.legacy.client.screen.HelpAndOptionsScreen;
import wily.legacy.client.screen.OptionsScreen;
import wily.legacy.client.screen.Panel;
import wily.legacy.client.screen.ScreenSection;

public class FiguraL4JQ implements IFiguraL4JQ {
	private static boolean madeMyChanges;
	@Override
	public void w(ScreenSection<?> l4jChangeScreen, Minecraft minecraft) {
		if (!madeMyChanges) {
			if (l4jChangeScreen instanceof OptionsScreen.Section s) {
				HelpAndOptionsScreen.CHANGE_SKIN = new OptionsScreen.Section(s.title(), (q) -> Panel.centered(q, 250, 165), s.elements(), s.advancedSection());
				s.elements().add(o -> o.getRenderableVList().renderables.add(0, new org.figuramc.figura.gui.widgets.Button(0, 0, 300, 16, Component.translatable("figura.gui.avatar_settings.tooltip"), null, b -> {
					if (minecraft.level != null) {
						minecraft.setScreen(new WardrobeScreen(Minecraft.getInstance().screen));
					} else {
						minecraft.setScreen(new ConfigScreen(Minecraft.getInstance().screen, false));
					}
				}) {
					@Override
					public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float delta) {
						CommonMatrixStack stack = CommonMatrixStack.of(gui);
						int x = this.getX();
						int y = this.getY();
						float speed = Configs.BACKGROUND_SCROLL_SPEED.tempValue * 0.125f;
						stack.pushPose();
						gui.enableScissor(x+1, y+1, x+width-1, y+height-1);
						stack.translate(0, 0, 999);
						for (ResourceLocation background : AbstractPanelScreen.BACKGROUNDS) {
							UIHelper.renderAnimatedBackground(gui, background, x, y, this.width, this.height, 64, 64, speed, delta);
							speed /= 0.5;
							//? if newera
							/*gui.nextStratum();*/
						}
						gui.disableScissor();
						stack.popPose();
						super.renderWidget(gui, mouseX, mouseY, delta);
					}
				}));
			}
			madeMyChanges = true;
		}
	}
}
//?}