package io.github.redrain0o0.legacyskins.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(targets = "com.tom.cpm.api.ClientApi$PlayerRendererImpl")
public interface PlayerRendererImplAccessor<M, RL> {
	@Accessor
	Map<M, RL> getTextureMap();
}
