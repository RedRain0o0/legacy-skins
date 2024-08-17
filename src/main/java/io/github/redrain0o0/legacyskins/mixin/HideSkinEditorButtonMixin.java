package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpm.client.CustomPlayerModelsClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CustomPlayerModelsClient.class, remap = false)
public class HideSkinEditorButtonMixin {

    @Inject(method = "lambda$onInitializeClient$5(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/screens/Screen;II)V", at = @At(value = "INVOKE", target = "Lnet/fabricmc/fabric/api/event/Event;register(Ljava/lang/Object;)V", shift = At.Shift.AFTER), cancellable = true)
    private static void RemoveEditorButton(CallbackInfo ci) {
        ci.cancel();
    }

}

