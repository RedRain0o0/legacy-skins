package io.github.redrain0o0.legacyskins.forge.mixin;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {ContainerEventHandler.class}, priority = 2147483647)
public abstract interface DummyInterfaceMixin {
}
