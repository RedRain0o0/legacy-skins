//? if >=1.21.6 {
/*package io.github.redrain0o0.legacyskins.mixin;

import io.github.redrain0o0.legacyskins.client.screen.GuiCpmSkinRenderState;
import io.github.redrain0o0.legacyskins.client.screen.GuiCpmSkinRenderer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if neoforge {
/^import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;^/
//?}

import java.util.List;

@Mixin(GuiRenderer.class)
public class GuiRendererMixin {
	@Shadow @Final private GuiRenderState renderState;
	@Unique
	private List<GuiCpmSkinRenderer> guiCpmSkinRenderers;
	@Inject(method = "<init>", at = @At("TAIL"))
	void init(GuiRenderState guiRenderState, MultiBufferSource.BufferSource bufferSource, List<PictureInPictureRenderer<?>> list, CallbackInfo ci) {
		// 10 skin renderers, if we need more, they will be created ad hoc
		guiCpmSkinRenderers = List.of(
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource),
				new GuiCpmSkinRenderer(bufferSource)
		);
	}

	@Inject(method = "close", at = @At("RETURN"))
	private void close(CallbackInfo ci) {
		guiCpmSkinRenderers.forEach(PictureInPictureRenderer::close);
	}

	@Inject(method = "preparePictureInPicture", at = @At("HEAD"))
	void preparePictureInPicture(CallbackInfo ci) {
		for (GuiCpmSkinRenderer guiCpmSkinRenderer : guiCpmSkinRenderers) {
			guiCpmSkinRenderer.ls$available();
		}
	}

	@Inject(method = "preparePictureInPictureState", at = @At("HEAD"), cancellable = true/^? if neoforge {^//^, remap = false^//^?}^/)
	void preparePictureInPictureState(PictureInPictureRenderState arg, int i, /^? if neoforge {^//^boolean firstPass, CallbackInfoReturnable<Boolean> cir^//^?} else {^/CallbackInfo ci/^?}^/) {
		if (arg.getClass() == GuiCpmSkinRenderState.class) {
			//noinspection resource
			GuiCpmSkinRenderer guiEntityRenderer = guiCpmSkinRenderers.stream().filter(GuiCpmSkinRenderer::ls$isAvailable).findFirst().orElseGet(() -> guiCpmSkinRenderers.getFirst()/^getFirst() can be used here since 1.21.7+ use Java 21^/.ls$spawnAnother());
			guiEntityRenderer.ls$use();
			guiEntityRenderer.prepare((GuiCpmSkinRenderState) arg, this.renderState, i);
			//? if neoforge {
			/^cir.setReturnValue(true);
			 ^///?} else {
			ci.cancel();
			//?}
		}
	}
}
*///?} else {
package io.github.redrain0o0.legacyskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class GuiRendererMixin {

}
//?}