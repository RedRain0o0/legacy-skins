//? if figurac && >=1.21.2 {
package io.github.redrain0o0.legacyskins.hacks;

import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.mixin.transformer.ext.IExtension;
import org.spongepowered.asm.mixin.transformer.ext.ITargetClassContext;

import java.lang.reflect.Field;
import java.util.SortedSet;

public class ItemInHandRendererHack implements IExtension {
	public static final Logger LOGGER = LoggerFactory.getLogger("legacy-skins-legacy4figura-hacks");
	@Override
	public boolean checkActive(MixinEnvironment environment) {
		return true;
	}

	@Override
	public void preApply(ITargetClassContext context) {
		try {
			preApplyo(context);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	private void preApplyo(ITargetClassContext context) throws Throwable {
		Class<?> aClass = Class.forName("org.spongepowered.asm.mixin.transformer.TargetClassContext");
		Field mixins = aClass.getDeclaredField("mixins");
		mixins.setAccessible(true);
		SortedSet<IMixinInfo> o = (SortedSet<IMixinInfo>) mixins.get(context);
		if (!o.removeIf(a -> a.getConfig().getMixinPackage().replace("/", ".").contains("wily.legacy.mixin.base") && a.getClassName().endsWith("PlayerRendererMixin"))) {
			LOGGER.debug("COULD NOT FIND L4J's mixin, removing our mixin");
			o.removeIf(a -> a.getConfig().getMixinPackage().replace("/", ".").contains("io.github.redrain0o0.legacyskins.mixin") && a.getClassName().endsWith("PlayerRendererMixin"));
		} else {
			LOGGER.debug("FOUND L4J's mixin, replaced with ours.");
		}
	}

	@Override
	public void postApply(ITargetClassContext context) {

	}

	@Override
	public void export(MixinEnvironment env, String name, boolean force, ClassNode classNode) {

	}
}
//?}