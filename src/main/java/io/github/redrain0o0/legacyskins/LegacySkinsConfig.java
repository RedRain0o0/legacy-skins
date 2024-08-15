package io.github.redrain0o0.legacyskins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LegacySkinsConfig {
	public static final Codec<LegacySkinsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(LegacySkin.CODEC.optionalFieldOf("skin").forGetter(a -> a.skin)).apply(instance, LegacySkinsConfig::new));
	// selected skin
	public Optional<LegacySkin> skin;

	public LegacySkinsConfig(Optional<LegacySkin> skin) {
		this.skin = skin;
	}

	public void setSkin(@Nullable LegacySkin skin) {
		this.skin = Optional.ofNullable(skin);
	}
}
