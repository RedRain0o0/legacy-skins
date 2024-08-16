package io.github.redrain0o0.legacyskins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record SkinReference(ResourceLocation pack, int ordinal) {
	public static final Codec<SkinReference> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("pack").forGetter(SkinReference::pack),
			Codec.INT.fieldOf("ordinal").forGetter(SkinReference::ordinal)
	).apply(instance, SkinReference::new));
}
