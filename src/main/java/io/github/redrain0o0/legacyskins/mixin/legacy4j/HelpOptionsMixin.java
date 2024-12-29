package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import io.github.redrain0o0.legacyskins.LegacySkinsConfig;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import io.github.redrain0o0.legacyskins.client.screen.ChangeSkinScreen;
import io.github.redrain0o0.legacyskins.client.screen.EScreen;
import io.github.redrain0o0.legacyskins.client.screen.NonLegacy4JChangeSkinScreen;
import io.github.redrain0o0.legacyskins.client.util.EasterEggUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy.client.screen./*$ l4joptionsscreen {*/HelpAndOptionsScreen/*$}*/;
import wily.legacy.client.screen.RenderableVList;
import wily.legacy.client.screen.RenderableVListScreen;

import java.util.function.Consumer;

@Mixin(/*$ l4joptionsscreen {*/HelpAndOptionsScreen/*$}*/.class)
public class HelpOptionsMixin extends RenderableVListScreen {
	public HelpOptionsMixin(Screen parent, Component component, Consumer<RenderableVList> vListBuild) {
		super(parent, component, vListBuild);
	}

	//? if legacy4j: <1.7.5 {
	/*@Inject(method = "lambda$new$1(Lnet/minecraft/client/gui/components/Button;)V", at = @At(value = "HEAD"), cancellable = true /^? if forge {^//^, remap = false^//^?}^/)
	private void ChangeSkinButton(Button b, CallbackInfo ci) {
		LegacySkinsClient.openScreen(this);
		ci.cancel();
	}
	*///?} else {
	@Inject(method = "lambda$new$1(Lnet/minecraft/client/gui/components/Button;)V", at = @At(value = "HEAD"), cancellable = true /*? if forge {*//*, remap = false*//*?}*/)
	private static void ChangeSkinButton(Button b, CallbackInfo ci) {
		LegacySkinsClient.openScreen(Minecraft.getInstance().screen);
		ci.cancel();
	}
	//?}
}