package io.github.redrain0o0.legacyskins.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record LegacySkin(ResourceLocation model) {
	public static final Codec<LegacySkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("model").forGetter(LegacySkin::model)
	).apply(instance, LegacySkin::new));
}
