package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import org.spongepowered.asm.mixin.Mixin;
import wily.legacy.Legacy4J;
import wily.legacy.Legacy4JClient;
//? if neoforge {
/*import wily.legacy.neoforge.Legacy4JForge;
import wily.legacy.neoforge.Legacy4JForgeClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
*///?} elif forge {
/*import wily.legacy.forge.Legacy4JForgeClient;
import net.minecraftforge.fml.loading.FMLLoader;
*///?}

@Mixin({
		//? if neoforge || forge
		/*Legacy4J.class, Legacy4JClient.class, Legacy4JForgeClient.class*/
		//? if fabric
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
