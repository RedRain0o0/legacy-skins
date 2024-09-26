package io.github.redrain0o0.legacyskins;

import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class LegacySkinsMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@SuppressWarnings({
			"SimplifiableConditionalExpression", // it's more readable like this, and compiles the same anyways
			"GrazieInspection" // who cares about "informal American English" anyways
	})
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return mixinClassName.contains("legacy4j") ? PlatformUtils.isModLoaded("legacy") : true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
