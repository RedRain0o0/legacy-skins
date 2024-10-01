package io.github.redrain0o0.legacyskins.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.util.SkinCollection;
import io.github.redrain0o0.legacyskins.mixin.ScreenAccessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

// Complete rewrite of the screen to fit into non-Legacy4J minecraft.
@ApiStatus.Experimental
public class NonLegacy4JChangeSkinScreen extends Screen {
	private final Screen parent;
	private @Nullable PlayerSkinWidgetList list;
	private boolean queuedChangeSkinPack;
	SkinCollection focusedSkinCollection;

	public NonLegacy4JChangeSkinScreen(Screen parent) {
		super(Component.literal("Change Skin"));
		this.parent = parent;
	}

	private SkinCollection getInitialSkinCollection() {
		SkinReference defaultSkin = new SkinReference(Constants.DEFAULT_PACK, 0);
		SkinReference currentSkin = Legacyskins.INSTANCE.getActiveSkinsConfig().getCurrentSkin().orElse(defaultSkin);
		if (SkinCollection.ofFavorites().has(currentSkin)) {
			return SkinCollection.ofFavorites();
		}
		return SkinCollection.ofSkinPack(defaultSkin.pack());
	}

	private Button leftButton;
	private Button rightButton;
	@Override
	protected void init() {
		super.init();
		//list = PlayerSkinWidgetList.of(width / 2, height / 2, skins.stream().map(a -> this.addRenderableWidget(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), () -> a))).toArray(PlayerSkinWidget[]::new));
		this.focusedSkinCollection = this.focusedSkinCollection == null ? getInitialSkinCollection() : this.focusedSkinCollection;
		skinPack(0);
		leftButton = Button.builder(Component.literal("<"), b -> {
			if (list != null) {
				list.sortForIndex(list.index - 1);
			}
		}).width(20).pos(80, height / 2 + 20).build();
		rightButton = Button.builder(Component.literal(">"), b -> {
			if (list != null) {
				list.sortForIndex(list.index + 1);
			}
		}).width(20).pos(width - 80 - 20, height / 2 + 20).build();
		addRenderableWidget(leftButton);
		addRenderableWidget(rightButton);
	}

	// index is the index of the skinpack
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
		if (list != null) {
			for (PlayerSkinWidget widget : list.widgets) {
				removeWidget(widget);
			}
		}
		int x = width / 2;
		int y = height / 2;
		List<SkinReference> skins = new ArrayList<>();
		int quota = 10;
		while (quota > 0) {
			int i = 0;
			if (this.focusedSkinCollection.isEmpty()) break;
			for (SkinReference ref : this.focusedSkinCollection.skins()) {
				skins.add(ref);
				quota--;
			}
		}
		int width = 327;
		int w2 = width / 2;
		int h2 = 150;
		int height = 300; // how is there going to be a height issue anyway
		addRenderableOnly(f = (guiGraphics, i, j, f) -> {
			guiGraphics.fill(x - w2, y - h2, x, y + h2, 0xf430abff);
			guiGraphics.fill(x, y - h2, x + w2, y + h2, 0xf260a9ae);
			guiGraphics.enableScissor(x - w2, y - h2, x+w2, y+h2);
		});
		if (quota > 0) {
			list = null;
		} else {
			list = PlayerSkinWidgetList.of(x - 43, y - 15, skins.stream().map(a -> this.addRenderableWidget(new PlayerSkinWidget(85, 120, this.minecraft.getEntityModels(), () -> a))).toArray(PlayerSkinWidget[]::new));
			list.sortForIndex(index);
		}
		addRenderableOnly(g = (guiGraphics, i, j, f) -> guiGraphics.disableScissor());
	}

	@Override
	public void render(GuiGraphics guiGraphics, int i, int j, float f) {
		super.render(guiGraphics, i, j, f);
	}

	//? if >=1.21 {
	@Override
	public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {
		super.renderBackground(guiGraphics, i, j, f);
		renderCoolAnimation(guiGraphics, i, j, f);
	}

	private void renderCoolAnimation(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
		System.out.println("oooooh");
		MultiBufferSource.BufferSource bufferSource = guiGraphics.bufferSource();
		PoseStack pose = guiGraphics.pose();
		Matrix4f last = pose.last().pose();
		Tesselator tesselator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
		bufferBuilder.addVertex(last, 15f, 15f, 5f).setColor(0x00454545);
		bufferBuilder.addVertex(last, 100, 100, 5).setColor(0x7F232323);
		bufferBuilder.addVertex(last, 305, 15, 5).setColor(0xFF989898);
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		BufferUploader.drawWithShader(bufferBuilder.build());


		//RenderSystem.setShaderColor(1, 1, 1, 1);
		//tesselator.
		//tesselator.clear();
		//VertexConsumer consumer = bufferSource.getBuffer(RenderType.gui())
	}
	//?}

	@Override
	public void onClose() {
		this.minecraft.setScreen(parent);
	}
}