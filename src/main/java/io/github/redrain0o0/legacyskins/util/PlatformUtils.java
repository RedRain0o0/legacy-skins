//? if fabric {
package io.github.redrain0o0.legacyskins.util;

import io.github.redrain0o0.legacyskins.Legacyskins;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public enum PlatformUtils {
	;
	public static Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}
	public static boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
	public static Path getGameDir() {
		return FabricLoader.getInstance().getGameDir();
	}
	public static Path findInMod(String path) {
		return FabricLoader.getInstance().getModContainer(Legacyskins.MOD_ID).orElseThrow().findPath(path).orElseThrow();
	}
}
//?} else if neoforge {
/*package io.github.redrain0o0.legacyskins.util;

import io.github.redrain0o0.legacyskins.Legacyskins;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public enum PlatformUtils {
	;
	public static Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}
	public static boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}
	public static Path getGameDir() {
		return FMLPaths.GAMEDIR.get();
	}

	public static Path findInMod(String path) {
		return ModList.get().getModContainerById(Legacyskins.MOD_ID).orElseThrow().getModInfo().getOwningFile().getFile().findResource(path);
	}
}
*/