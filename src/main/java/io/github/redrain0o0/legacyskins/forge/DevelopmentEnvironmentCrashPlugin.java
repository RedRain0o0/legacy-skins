package io.github.redrain0o0.legacyskins.forge;

import cpw.mods.modlauncher.api.INameMappingService;
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import wily.legacy.mixin.OptionsMixin;

import java.util.List;
import java.util.Set;

public class DevelopmentEnvironmentCrashPlugin implements IMixinConfigPlugin {
	public static final Logger LOGGER = LoggerFactory.getLogger("Legacy Skins Forge Dev Mixin Plugin");

	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	// Any method calls to obfuscated method names? Record them here!
	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		for (MethodNode method : targetClass.methods) {
//			if (method.name.equals("handler$zij000$init")) {
//				System.out.println("FOUND THE METHOD");
//				for (AbstractInsnNode instruction : method.instructions) {
//					if (instruction instanceof MethodInsnNode methodInsnNode) {
//						System.out.println(methodInsnNode.name);
//					}
//				}
//			}
			for (AbstractInsnNode instruction : method.instructions) {
				if (instruction instanceof MethodInsnNode methodInsnNode) {
					String name = methodInsnNode.name;
					if (name.startsWith("m_")) {
						LOGGER.info("Found obfuscated method: " + name);
						methodInsnNode.name = fixMethodName(name);
					}
				}
				if (instruction instanceof InvokeDynamicInsnNode node) {
					String name = node.name;
					Object[] bsmArgs = node.bsmArgs;
					for (int i = 0; i < bsmArgs.length; i++) {
						Object bsmArg = bsmArgs[i];
						if (bsmArg instanceof Handle handle) {
							if (handle.getName().startsWith("m_")) {
								LOGGER.info("Found obfuscated method: " + handle);
								bsmArgs[i] = new Handle(handle.getTag(), handle.getOwner(), fixMethodName(handle.getName()), handle.getDesc(), handle.isInterface());
							}
						}
						//System.out.println(bsmArg + " " + bsmArg.getClass());
					}
					if (name.startsWith("m_")) {
						LOGGER.info("Found obfuscated method: " + name);
					}
				}
			}
		}
	}

	private static String fixMethodName(String obfuscatedMethodName) {
		return ObfuscationReflectionHelper.remapName(INameMappingService.Domain.METHOD, obfuscatedMethodName);
	}
}
