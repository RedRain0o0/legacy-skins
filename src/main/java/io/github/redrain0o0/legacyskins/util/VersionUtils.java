package io.github.redrain0o0.legacyskins.util;

import net.minecraft.resources.ResourceLocation;

public enum VersionUtils {
	;
	public static ResourceLocation of(String namespace, String path) {
		//? if <1.21
		return new ResourceLocation(namespace, path);
		//? if >=1.21
		/*return ResourceLocation.fromNamespaceAndPath(namespace, path);*/
	}

	public static ResourceLocation ofMinecraft(String path) {
		return of("minecraft", path);
	}

	public static ResourceLocation parse(String path) {
		//? if <1.21
		return new ResourceLocation(path);
		//? if >=1.21
		/*return ResourceLocation.parse(path);*/
	}
}
