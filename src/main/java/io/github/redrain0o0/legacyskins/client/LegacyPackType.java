package io.github.redrain0o0.legacyskins.client;

import com.mojang.serialization.Codec;
import net.minecraft.Util;

import java.util.Locale;

public enum LegacyPackType {
	DEFAULT,
	SKIN,
	MASHUP,
	DEV;
	public static final Codec<LegacyPackType> CODEC = Codec.STRING.xmap(a -> LegacyPackType.valueOf(a.toUpperCase(Locale.ROOT)), a -> a.name().toLowerCase(Locale.ROOT));
	public String translationKey() {
		return "legacyskins.packType.%s".formatted(this.name().toLowerCase(Locale.ROOT));
	}
}
