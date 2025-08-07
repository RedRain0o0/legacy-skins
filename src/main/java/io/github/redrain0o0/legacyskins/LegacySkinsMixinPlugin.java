package io.github.redrain0o0.legacyskins;

import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

//? if figurac {
//? if >=1.21.2 {
import io.github.redrain0o0.legacyskins.hacks.ItemInHandRendererHack;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.ext.Extensions;
//?}
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
//?}

//? if neoforge {
/*import net.neoforged.fml.loading.FMLLoader;
*///?} elif forge {
/*import net.minecraftforge.fml.loading.FMLLoader;
 *///?}

public class LegacySkinsMixinPlugin implements IMixinConfigPlugin {
	private boolean legacyLoaded;
	//? if figurac
	private boolean figuraLoaded;
	private boolean cpmLoaded;
	@Override
	public void onLoad(String mixinPackage) {
		legacyLoaded = PlatformUtils.isModLoaded("legacy");
		//? if figurac
		figuraLoaded = PlatformUtils.isModLoaded("figura");
		//? if figurac && >=1.21.2 {
		if (legacyLoaded && figuraLoaded) {
			((Extensions) ((IMixinTransformer) MixinEnvironment.getDefaultEnvironment().getActiveTransformer()).getExtensions()).add(new ItemInHandRendererHack());
		}
		//?}
		cpmLoaded = PlatformUtils.isModLoaded("cpm");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@SuppressWarnings("SpellCheckingInspection")
	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		//? if figurac && >=1.21.2 {
		if (mixinClassName.endsWith("PlayerRendererMixin")) return figuraLoaded && legacyLoaded;
		//?} else {
		/*if (mixinClassName.endsWith("PlayerRendererMixin")) return false;
		*///?}
		//? if legacy4j: >=1.7.5 && (forge || neoforge) {
		/*if (!FMLLoader.launcherHandlerName().contains("data")) {
			if (mixinClassName.contains("Legacy4JClientMixin")) return false;
		}
		*///?}
		//? if figurac
		if (mixinClassName.contains("figura")) return figuraLoaded;
		if (targetClassName.startsWith("com.tom.cpm") || targetClassName.startsWith("com.tom.cpl")) return cpmLoaded;
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
		//? if figurac {
		if (targetClassName.endsWith("LocalAvatarLoader")) {
			targetClass.methods.removeIf(a -> a.invisibleAnnotations != null && a.invisibleAnnotations.stream().anyMatch(b -> "Lio/github/redrain0o0/legacyskins/annotations/SurrogateMethod;".equals(b.desc)));
			MethodNode methodNode = targetClass.methods.stream().filter(a -> "loadAvatar".equals(a.name) && "(Ljava/nio/file/Path;Lorg/figuramc/figura/avatar/UserData;)V".equals(a.desc)).findFirst().orElseThrow();
			InvokeDynamicInsnNode invokeDynamicInsnNode = Arrays.stream(methodNode.instructions.toArray()).filter(a -> a instanceof InvokeDynamicInsnNode node && "run".equals(node.name) && "(Ljava/nio/file/Path;Lorg/figuramc/figura/avatar/UserData;)Ljava/lang/Runnable;".equals(node.desc)).map(a -> (InvokeDynamicInsnNode) a).findFirst().orElseThrow();
			Handle bsmArg = (Handle) invokeDynamicInsnNode.bsmArgs[1];
			MethodNode methodNode1 = targetClass.methods.stream().filter(a -> a.name.equals(bsmArg.getName()) && a.desc.equals(bsmArg.getDesc())).findFirst().orElseThrow();
			MethodVisitor ls$loadAvatar = targetClass.visitMethod(methodNode1.access & ~((methodNode1.access & Opcodes.ACC_SYNTHETIC) != 0 ? Opcodes.ACC_SYNTHETIC : 0), "ls$loadAvatar", methodNode1.desc, methodNode1.signature, methodNode1.exceptions.toArray(String[]::new));
			ls$loadAvatar.visitVarInsn(Opcodes.ALOAD, 0);
			ls$loadAvatar.visitVarInsn(Opcodes.ALOAD, 1);
			ls$loadAvatar.visitMethodInsn(Opcodes.INVOKESTATIC, targetClassName.replace(".", "/"), methodNode1.name, methodNode1.desc, false);
			ls$loadAvatar.visitInsn(Opcodes.RETURN);
			ls$loadAvatar.visitMaxs(0, 0);
		}
		//?}
	}
}
