package io.github.redrain0o0.legacyskins.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

import java.util.Optional;

public record LegacySkin(Type type, ResourceLocation model, Optional<String> creditsLink) {
	public static final Codec<LegacySkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Type.CODEC.fieldOf("modelType").forGetter(LegacySkin::type),
			ResourceLocation.CODEC.fieldOf("model").forGetter(LegacySkin::model),
			Codec.STRING.optionalFieldOf("creditsLink").forGetter(LegacySkin::creditsLink)
	).apply(instance, LegacySkin::new));

	public LegacySkin(ResourceLocation model) {
		this(model, Optional.empty());
	}

	public LegacySkin(ResourceLocation model, Optional<String> creditsLink) {
		this(Type.CPM, model, creditsLink);
	}

	public enum Type implements StringRepresentable {
		CPM,
		FIGURA;
		public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);

		@Override
		public String getSerializedName() {
			return this == CPM ? "cpm" : "figura";
		}
	}

	public boolean canBeUsed() {
		if (this == Constants.FALLBACK_SKIN) return false;
		return type() == Type.CPM ? PlatformUtils.isModLoaded("cpm") : /*? if figurac {*/PlatformUtils.isModLoaded("figura")/*?} else {*//*false*//*?}*/;
	}
}
