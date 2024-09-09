//? if forge {
package io.github.redrain0o0.legacyskins.forge.mixin;

import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import wily.legacy.client.screen.RenderableVList;

@Mixin(value = {Options.class, RenderableVList.class}, priority = 2147483647)
public abstract class DummyMixin {
}
//?