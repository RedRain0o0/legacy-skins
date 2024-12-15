package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.legacy.client.screen.Assort;

@Mixin(Assort.class)
public class AssortMixin {
	@Inject(method = "updateSavedResourcePacks", at = @At("RETURN"), remap = false)
	private static void updateSavedResourcePacksInject(CallbackInfo ci) {
		Minecraft.getInstance().options.resourcePacks.addAll(Legacyskins.lazyInstance().downloadedPacks().values().stream().map(a -> "file/" + a).toList());
	}
}
