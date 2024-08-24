package io.github.redrain0o0.legacyskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import wily.legacy.client.screen.RenderableVList;
import wily.legacy.util.Stocker;

@Mixin(RenderableVList.class)
public interface RenderableVListAccessor {
	@Accessor
	Stocker<Integer> getScrolledList();
	@Accessor
	int getRenderablesCount();
}
