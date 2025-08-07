package io.github.redrain0o0.legacyskins.mixin;

import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
	@Inject(method = "tick", at = @At("RETURN"))
	void ti(BooleanSupplier booleanSupplier, CallbackInfo ci) {
		LegacySkinsClient.generalFiguraQ.q3();
	}
}
