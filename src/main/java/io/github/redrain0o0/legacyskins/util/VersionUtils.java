package io.github.redrain0o0.legacyskins.util;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

//? if <=1.20.4 {
/*import java.util.function.Function;
import com.mojang.datafixers.util.Either;
*///?}

public enum VersionUtils {
	;
	public static ResourceLocation of(String namespace, String path) {
		//? if <1.21
		/*return new ResourceLocation(namespace, path);*/
		//? if >=1.21
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	public static ResourceLocation ofMinecraft(String path) {
		return of("minecraft", path);
	}

	public static ResourceLocation parse(String path) {
		//? if <1.21
		/*return new ResourceLocation(path);*/
		//? if >=1.21
		return ResourceLocation.parse(path);
	}
	//? if <=1.20.4 {
	/*public static <T> Codec<T> withAlternative(final Codec<T> primary, final Codec<? extends T> alternative) {
		return Codec.either(
				primary,
				alternative
		).xmap(
				VersionUtils::unwrap,
				Either::left
		);
	}

	private static <U> U unwrap(final Either<? extends U, ? extends U> either) {
		return either.map(Function.identity(), Function.identity());
	}
	*///?} else {
	public static <T> Codec<T> withAlternative(final Codec<T> primary, final Codec<? extends T> alternative) {
		return Codec.withAlternative(primary, alternative);
	}
	//?}
}
