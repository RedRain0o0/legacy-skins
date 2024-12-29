package io.github.redrain0o0.legacyskins;

import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

//? if neoforge {
/*import net.neoforged.fml.loading.FMLLoader;
*///?} elif forge {
/*import net.minecraftforge.fml.loading.FMLLoader;
 *///?}

public class LegacySkinsMixinPlugin implements IMixinConfigPlugin {
	private boolean legacyLoaded;
	@Override
	public void onLoad(String mixinPackage) {
		legacyLoaded = PlatformUtils.isModLoaded("legacy");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		//? if legacy4j: >=1.7.5 && (forge || neoforge) {
		/*if (!FMLLoader.launcherHandlerName().contains("data")) {
			if (mixinClassName.contains("Legacy4JClientMixin")) return false;
		}
		*///?}
		return mixinClassName.contains("nonlegacy4j") ? !legacyLoaded : !mixinClassName.contains("legacy4j") || legacyLoaded;
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
