package io.github.redrain0o0.legacyskins.mixin.legacy4j;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import wily.factoryapi.base.client.AdvancedTextWidget;
import wily.legacy.client.LegacyTip;

@Mixin(LegacyTip.class)
public interface LegacyTipAccessor {
	@Accessor
	AdvancedTextWidget getTipLabel();
}
