package io.github.redrain0o0.legacyskins.mixin;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.legacy.Legacy4J;
import wily.legacy.Legacy4JClient;
//? if neoforge {
/*import wily.legacy.neoforge.Legacy4JForge;
import wily.legacy.neoforge.Legacy4JForgeClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
*///?}

@Mixin({
		//? if neoforge
		/*Legacy4J.class, Legacy4JClient.class, Legacy4JForgeClient.class*/
		//? if fabric
		Legacy4J.class
})
public class Legacy4JForgeMixin {
	//? if neoforge {
	/*@Inject(method = {"init"}, at = @At("HEAD"), cancellable = true)
	private static void init(CallbackInfo ci) {
		System.out.println(FMLLoader.launcherHandlerName());
		if (FMLLoader.launcherHandlerName().contains("data"))
			ci.cancel();
	}
	*///?}
}
