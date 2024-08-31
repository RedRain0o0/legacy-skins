package io.github.redrain0o0.legacyskins;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * @param pack The resource location of the pack.
 * @param ordinal The ordinal of the skin in the pack (Starts from 0!)
 */
public record SkinReference(ResourceLocation pack, int ordinal) {
	public static final Codec<SkinReference> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("pack").forGetter(SkinReference::pack),
			Codec.INT.fieldOf("ordinal").forGetter(SkinReference::ordinal)
	).apply(instance, SkinReference::new));
}
