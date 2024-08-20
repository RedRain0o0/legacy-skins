package io.github.redrain0o0.legacyskins.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public record LegacySkin(ResourceLocation model, Optional<Cape> cape) {
	public static final Codec<LegacySkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("model").forGetter(LegacySkin::model),
			Cape.CODEC.optionalFieldOf("cape").forGetter(LegacySkin::cape)
	).apply(instance, LegacySkin::new));

	public LegacySkin(ResourceLocation model) {
		this(model, Optional.empty());
	}

	public record Cape(ResourceLocation texture, boolean elytra) {
		public static final Codec<Cape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				ResourceLocation.CODEC.fieldOf("texture").forGetter(Cape::texture),
				Codec.BOOL.optionalFieldOf("elytra", true).forGetter(Cape::elytra)
		).apply(instance, Cape::new));
	}
}
