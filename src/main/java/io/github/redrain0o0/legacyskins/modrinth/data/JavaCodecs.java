package io.github.redrain0o0.legacyskins.modrinth.data;

import com.mojang.serialization.Codec;

import java.awt.Color;
import java.time.Instant;

public class JavaCodecs {
	public static final Codec<Instant> INSTANT = Codec.STRING.xmap(Instant::parse, Instant::toString);
	public static final Codec<Color> COLOR = Codec.INT.xmap(Color::new, f -> f.getRGB() & 0x00FFFFFF);
}