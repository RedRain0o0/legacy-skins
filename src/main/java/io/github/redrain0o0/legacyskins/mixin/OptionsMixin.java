package io.github.redrain0o0.legacyskins.mixin;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Options.class, priority = 800)
public class OptionsMixin {
	@Inject(method = "loadSelectedResourcePacks", at = @At("HEAD"))
	void loadSelectedResourcePacksInject(PackRepository packRepository, CallbackInfo ci) {
		System.out.println("OUR INJECT WORKED!");
		Minecraft.getInstance().options.resourcePacks.addAll(Legacyskins.lazyInstance().downloadedPacks().values().stream().map(a -> "file/" + a).toList());
		Legacyskins.lazyInstance().downloadedPacks().values().stream().map(a -> "file/" + a).forEachOrdered(packRepository::addPack);
		LegacySkinsClient.singleFireAssortApply = true;
	}
}
