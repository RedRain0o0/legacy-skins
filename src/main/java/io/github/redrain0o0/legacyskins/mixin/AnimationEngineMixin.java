package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.animation.AnimationEngine;
import com.tom.cpm.shared.network.NetHandler;
import com.tom.cpm.shared.network.ServerCaps;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AnimationEngine.class, remap = false)
public class AnimationEngineMixin {
	//@Expression("")
	// MinecraftClientAccess.get().getNetHandler().hasServerCap(ServerCaps.GESTURES)
	@Redirect(method = "prepareAnimations", at = @At(value = "INVOKE", target = "Lcom/tom/cpm/shared/network/NetHandler;hasServerCap(Lcom/tom/cpm/shared/network/ServerCaps;)Z"))
	boolean prepareAnimations(NetHandler<?, ?, ?> instance, ServerCaps cap) {
		if (Minecraft.getInstance().getConnection() != null) {
			return instance.hasServerCap(cap);
		}
		return true;
	}

	@Redirect(method = "handleAnimation", at = @At(value = "INVOKE", target = "Lcom/tom/cpm/shared/network/NetHandler;hasServerCap(Lcom/tom/cpm/shared/network/ServerCaps;)Z"))
	boolean handleAnimation(NetHandler<?, ?, ?> instance, ServerCaps cap) {
		if (Minecraft.getInstance().getConnection() != null) {
			return instance.hasServerCap(cap);
		}
		return true;
	}
}
