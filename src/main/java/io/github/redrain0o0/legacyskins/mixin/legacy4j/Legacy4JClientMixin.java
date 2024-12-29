package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if neoforge {

//?} elif forge {
/*import net.minecraftforge.fml.loading.FMLLoader;*/
//?}

//? if legacy4j: >=1.7.5 {
import wily.factoryapi.base.client.UIDefinition;
import wily.legacy.client.screen.LegacyLoadingScreen;
//?}

@Mixin({
		/*? if legacy4j: >=1.7.5 {*/ LegacyLoadingScreen.class/*?} else {*//*MinecraftServer.class*//*?}*/
})
public class Legacy4JClientMixin {
	//? if legacy4j: >=1.7.5 && (neoforge || forge) {
	/*@Redirect(method = {"<init>()V"}, at = @At(value = "INVOKE", target = "wily/factoryapi/base/client/UIDefinition$Accessor.of(Lnet/minecraft/client/gui/screens/Screen;)Lwily/factoryapi/base/client/UIDefinition$Accessor;"), remap = false)
	private UIDefinition.Accessor init(Screen screen) {
		return null;
	}
	*///?}
}
