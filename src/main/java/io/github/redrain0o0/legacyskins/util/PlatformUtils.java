package io.github.redrain0o0.legacyskins.util;

import io.github.redrain0o0.legacyskins.Legacyskins;
//? if fabric {
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
//?} elif neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.LoadingModList;
*///?}

import java.nio.file.Path;
import java.util.function.Supplier;

public enum PlatformUtils {
	;
	public static Path getConfigDir() {
		//? if fabric
		return FabricLoader.getInstance().getConfigDir();
		//? if neoforge
		/*return FMLPaths.CONFIGDIR.get();*/
	}
	public static boolean isDevelopmentEnvironment() {
		//? if fabric
		return FabricLoader.getInstance().isDevelopmentEnvironment();
		//? if neoforge
		/*return !FMLLoader.isProduction();*/
	}
	public static Path getGameDir() {
		//? if fabric
		return FabricLoader.getInstance().getGameDir();
		//? if neoforge
		/*return FMLPaths.GAMEDIR.get();*/
	}

	public static Path findInMod(String path) {
		//? if fabric
		return FabricLoader.getInstance().getModContainer(Legacyskins.MOD_ID).orElseThrow().findPath(path).orElseThrow();
		//? if neoforge
		/*return ModList.get().getModContainerById(Legacyskins.MOD_ID).orElseThrow().getModInfo().getOwningFile().getFile().findResource(path);*/
	}

	public static Env fromPlatformSpecific(/*? if fabric {*/ EnvType env /*?} elif neoforge {*//*Dist dist *//*?}*/) {
		//? if fabric
		return env == EnvType.CLIENT ? Env.CLIENT : env == EnvType.SERVER ? Env.SERVER : null;
		//? if neoforge
		/*return dist == Dist.CLIENT ? Env.CLIENT : dist == Dist.DEDICATED_SERVER ? Env.SERVER : null;*/
	}

	public static Env getEnv() {
		//? if fabric
		return fromPlatformSpecific(FabricLoader.getInstance().getEnvironmentType());
		//? if neoforge
		/*return fromPlatformSpecific(FMLLoader.getDist());*/
	}

	public static void executeInDist(Env env, Supplier<Supplier<Runnable>> toRun) {
		if (getEnv() == env) {
			toRun.get().get().run();
		}
	}

	public static <T> T getInDist(Env env, Supplier<Supplier<Supplier<T>>> toGet) {
		if (getEnv() == env) {
			return toGet.get().get().get();
		}
		return null;
	}

	public static boolean isModLoaded(String id) {
		//? if fabric
		return FabricLoader.getInstance().isModLoaded(id);
		//? if neoforge {
		/*// NeoForge might have renamed the class
		//noinspection Convert2MethodRef
		return LoadingModList.get().getMods().stream().map(a -> a.getModId()).anyMatch(id::equals);
		*///?}
	}

	public enum Env {
		CLIENT,
		SERVER
	}
}
