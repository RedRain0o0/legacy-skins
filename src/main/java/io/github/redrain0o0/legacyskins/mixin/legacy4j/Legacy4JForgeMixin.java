package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy.Legacy4J;
import wily.legacy.Legacy4JClient;
//? if neoforge {
/*import net.neoforged.fml.loading.FMLLoader;
//? if legacy4j: <1.7.5
/^import wily.legacy.neoforge.Legacy4JForgeClient;^/
*///?} elif forge {
/*//? if legacy4j: <1.7.5
/^import wily.legacy.forge.Legacy4JForgeClient;^/
import net.minecraftforge.fml.loading.FMLLoader;
*///?}

@Mixin({
		//? if neoforge || forge {
		/*Legacy4J.class, Legacy4JClient.class /^? if legacy4j: <1.7.5 {^//^, Legacy4JForgeClient.class^//^?}^/
		*///?} elif fabric
		Legacy4J.class
})
public class Legacy4JForgeMixin {
	//? if neoforge || forge {
	/*@Inject(method = {"init"}, at = @At("HEAD"), cancellable = true, remap = false)
	private static void init(CallbackInfo ci) {
		// System.out.println(FMLLoader.launcherHandlerName());
		if (FMLLoader.launcherHandlerName().contains("data"))
			ci.cancel();
	}
	*///?}
}
