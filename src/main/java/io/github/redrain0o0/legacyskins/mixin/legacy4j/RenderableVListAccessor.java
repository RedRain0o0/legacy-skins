package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import wily.legacy.client.screen.RenderableVList;
//? if legacy4j: >=1.7.5 {
import wily.factoryapi.base.Stocker;
//?} else {
/*import wily.legacy.util.Stocker;
*///?}

@Mixin(RenderableVList.class)
public interface RenderableVListAccessor {
	@Accessor
	Stocker<Integer> getScrolledList();
	@Accessor
	int getRenderablesCount();
	@Accessor("canScrollDown")
	boolean canScrollDown();
}
