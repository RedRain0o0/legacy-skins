package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpm.shared.network.NetHandler;
import com.tom.cpm.shared.network.ServerCaps;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NetHandler.class, remap = false)
public class NetHandlerMixin {
	@Inject(method = "hasServerCap", at = @At("HEAD"), cancellable = true)
	private void hasServerCap(ServerCaps cap, CallbackInfoReturnable<Boolean> cir) {
		if (Minecraft.getInstance().getConnection() == null) cir.setReturnValue(true);
	}
}
