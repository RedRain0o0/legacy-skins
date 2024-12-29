package io.github.redrain0o0.legacyskins.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
//? if <1.20.2
/*import wily.legacy.client.LegacyGuiGraphics;*/

public interface GuiGraphicsUtils {
	static GuiGraphicsUtils ofGuiGraphics(GuiGraphics guiGraphics) {
		return new GuiGraphicsUtils() {
			@Override
			public void blitSprite(ResourceLocation resourceLocation, int x, int y, int width, int height) {
				//? if >=1.21.2 {
				guiGraphics.blitSprite(RenderType::guiTextured, resourceLocation, x, y, width, height);
				//?} else
				/*p(guiGraphics).blitSprite(resourceLocation, x, y, width, height);*/
			}

			@Override
			public void blit(ResourceLocation resourceLocation, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight) {
				//? if >=1.21.2 {
				guiGraphics.blit(RenderType::guiTextured, resourceLocation, x, y, u, v, width, height, texWidth, texHeight);
				//?} else
				/*guiGraphics.blit(resourceLocation, x, y, u, v, width, height, texWidth, texHeight);*/
			}

			private /*? if >=1.20.2 {*/ GuiGraphics /*?} else {*/ /*LegacyGuiGraphics *//*?}*/ p(GuiGraphics in) {
				//? if >=1.20.2 {
				return in;
				 //?} else
				/*return LegacyGuiGraphics.of(in);*/
			}
		};
	}

	void blitSprite(ResourceLocation resourceLocation, int x, int y, int width, int height);

	void blit(ResourceLocation resourceLocation, int x, int y, int u, int v, int width, int height, int texWidth, int texHeight);
}
