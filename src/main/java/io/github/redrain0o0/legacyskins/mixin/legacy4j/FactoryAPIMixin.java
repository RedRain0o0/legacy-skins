package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import net.minecraft.server.MinecraftServer;
//? if neoforge {
/*import net.neoforged.fml.loading.FMLLoader;
*///?} elif forge {
/*import net.minecraftforge.fml.loading.FMLLoader;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if legacy4j: >=1.7.5
import wily.factoryapi.FactoryAPIClient;

@Mixin({
		/*? if legacy4j: >=1.7.5 {*/ FactoryAPIClient.class/*?} else {*//*MinecraftServer.class*//*?}*/
})
public class FactoryAPIMixin {
	//? if legacy4j: >=1.7.5 && (neoforge || forge) {
	/*@Inject(method = {"init"}, at = @At("HEAD"), cancellable = true, remap = false)
	private static void init(CallbackInfo ci) {
		// System.out.println(FMLLoader.launcherHandlerName());
		if (FMLLoader.launcherHandlerName().contains("data"))
			ci.cancel();
	}
	*///?}
}
