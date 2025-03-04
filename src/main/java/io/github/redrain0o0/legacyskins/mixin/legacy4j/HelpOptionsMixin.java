package io.github.redrain0o0.legacyskins.mixin.legacy4j;

//? if !forge {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
//?} else {
/*import org.spongepowered.asm.mixin.injection.Redirect;
*///?}
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wily.legacy.client.screen./*$ l4joptionsscreen {*/HelpAndOptionsScreen/*$}*/;
import wily.legacy.client.screen.RenderableVList;
import wily.legacy.client.screen.RenderableVListScreen;

import java.util.function.Consumer;

@Mixin(/*$ l4joptionsscreen {*/HelpAndOptionsScreen/*$}*/.class)
public class HelpOptionsMixin extends RenderableVListScreen {
	public HelpOptionsMixin(Screen parent, Component component, Consumer<RenderableVList> vListBuild) {
		super(parent, component, vListBuild);
	}

	//? if forge {
	/*@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lwily/legacy/client/screen/RenderableVList;addRenderable(Lnet/minecraft/client/gui/components/Renderable;)Lwily/legacy/client/screen/RenderableVList;", ordinal = 0))
	private RenderableVList ChangeSkinButton(RenderableVList instance, Renderable renderable) {
		instance.addRenderable(openScreenButton(Component.translatable("legacy.menu.change_skin"), () -> {
			return LegacySkinsClient.getSkinsScreen(this);
		}).build());
		return instance;
	}
	*///?} else {
	@WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lwily/legacy/client/screen/RenderableVList;addRenderable(Lnet/minecraft/client/gui/components/Renderable;)Lwily/legacy/client/screen/RenderableVList;", ordinal = 0))
	private RenderableVList ChangeSkinButton(RenderableVList instance, Renderable renderable, Operation<RenderableVList> original) {
		original.call(instance, openScreenButton(Component.translatable("legacy.menu.change_skin"), () -> {
			return LegacySkinsClient.getSkinsScreen(this);
		}).build());
		return instance;
	}
	//?}
}