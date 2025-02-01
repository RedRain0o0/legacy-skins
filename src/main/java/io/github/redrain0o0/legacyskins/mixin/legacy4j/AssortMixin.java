package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.legacy.client.PackAlbum;

import java.util.ArrayList;
import java.util.List;

@Mixin(PackAlbum.class)
public class AssortMixin {
	@Inject(method = "updateSavedResourcePacks", at = @At("RETURN"), remap = false)
	private static void updateSavedResourcePacksInject(CallbackInfo ci) {
		Minecraft.getInstance().options.resourcePacks.addAll(Legacyskins.lazyInstance().downloadedPacks().values().stream().map(a -> "file/" + a).toList());
		System.out.println("INJECTED PACKS " + "DA");
	}

	@Inject(method = "init", at = @At("RETURN"), remap = false)
	private static void initInject(CallbackInfo ci) {
		Minecraft.getInstance().options.resourcePacks.addAll(Legacyskins.lazyInstance().downloadedPacks().values().stream().map(a -> "file/" + a).toList());
		System.out.println("INEJCTACP PACKS " + "LF");
	}

	@Inject(method = "packs", at = @At("RETURN"), remap = false, cancellable = true)
	private void packsInject(CallbackInfoReturnable<List<String>> cir) {
		if (LegacySkinsClient.singleFireAssortApply) {
			List<String> returnValue = cir.getReturnValue();
			ArrayList<String> strings = new ArrayList<>(returnValue);
			Legacyskins.lazyInstance().downloadedPacks().values().stream().map(a -> "file/" + a).forEachOrdered(strings::add);
			cir.setReturnValue(strings);
			LegacySkinsClient.singleFireAssortApply = false;
		}
	}
}
