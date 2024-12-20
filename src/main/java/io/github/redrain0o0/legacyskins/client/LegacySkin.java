package io.github.redrain0o0.legacyskins.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record LegacySkin(ResourceLocation model, Optional<String> creditsLink) {
	public static final Codec<LegacySkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("model").forGetter(LegacySkin::model),
			Codec.STRING.optionalFieldOf("creditsLink").forGetter(LegacySkin::creditsLink)
	).apply(instance, LegacySkin::new));

	public LegacySkin(ResourceLocation model) {
		this(model, Optional.empty());
	}
}
