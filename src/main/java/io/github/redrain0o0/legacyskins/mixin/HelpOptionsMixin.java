package io.github.redrain0o0.legacyskins.mixin;

import io.github.redrain0o0.legacyskins.client.screen.ChangeSkinScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy.client.screen.HelpOptionsScreen;
import wily.legacy.client.screen.RenderableVList;
import wily.legacy.client.screen.RenderableVListScreen;

import java.util.function.Consumer;

@Mixin(HelpOptionsScreen.class)
public class HelpOptionsMixin extends RenderableVListScreen {
	public HelpOptionsMixin(Screen parent, Component component, Consumer<RenderableVList> vListBuild) {
		super(parent, component, vListBuild);
	}

	//@ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;", ordinal = 0), index = 0)
	@Inject(method = "lambda$new$1(Lnet/minecraft/client/gui/components/Button;)V", at = @At(value = "HEAD"), cancellable = true)
	private void ChangeSkinButton(Button b, CallbackInfo ci) {
		this.minecraft.setScreen(new ChangeSkinScreen(this));
		ci.cancel();
		//return Button.builder(Component.translatable("legacy.menu.change_skin"),(b)-> minecraft.getToasts().addToast(new LegacyTip(Component.literal("Work is progressing!!"), 80, 40).disappearTime(960))).build();
		//return openScreenButton(Component.translatable("legacy.menu.change_skin"),()->new ChangeSkinScreen(this)).build();
	}
}