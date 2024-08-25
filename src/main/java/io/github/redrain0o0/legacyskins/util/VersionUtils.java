package io.github.redrain0o0.legacyskins.util;

import net.minecraft.resources.ResourceLocation;

public enum VersionUtils {
	;
	public static ResourceLocation of(String namespace, String path) {
		return new ResourceLocation(namespace, path);
	}

	public static ResourceLocation ofMinecraft(String path) {
		return of("minecraft", path);
	}

	public static ResourceLocation parse(String path) {
		return new ResourceLocation(path);
	}
}
