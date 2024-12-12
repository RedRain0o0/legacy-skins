package io.github.redrain0o0.legacyskins.client.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

// Here due to class loading conflicts
public class SortingOrderCodecs {
	private static final Codec<SortingOrder.Absolute<ResourceLocation>> COMPACT = Codec.DOUBLE.xmap(SortingOrder.Absolute::new, SortingOrder.Absolute::pos);
	private static final Codec<SortingOrder.Absolute<ResourceLocation>> EXPANDED = RecordCodecBuilder.create(instance -> instance.group(
			Codec.DOUBLE.fieldOf("absolute").forGetter(SortingOrder.Absolute::pos)
	).apply(instance, SortingOrder.Absolute::new));
	private static final Codec<SortingOrder.Absolute<ResourceLocation>> ABSOLUTE_CODEC = Codec.withAlternative(COMPACT, EXPANDED);
	private static final Codec<SortingOrder.Before<ResourceLocation>> BEFORE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("before").forGetter(SortingOrder.Before::t)
	).apply(instance, SortingOrder.Before::new));
	private static final Codec<SortingOrder.After<ResourceLocation>> AFTER_CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("after").forGetter(SortingOrder.After::t)
	).apply(instance, SortingOrder.After::new));
	private static final Codec<SortingOrder<ResourceLocation>> BEFORE_AFTER = Codec.either(BEFORE_CODEC, AFTER_CODEC).xmap(f -> {
		if (f.left().isPresent()) return f.left().get();
		if (f.right().isPresent()) return f.right().get();
		return null;
	}, f -> {
		if (f instanceof SortingOrder.After<ResourceLocation> after) return Either.right(after);
		if (f instanceof SortingOrder.Before<ResourceLocation> before) return Either.left(before);
		return null;
	});

	// examples:
	// 15
	// 15d
	// {"after": "example:pack1"}
	// {"absolute": 50}
	// {"before": "example:pack1"}
	public static final Codec<SortingOrder<ResourceLocation>> CODEC = Codec.either(ABSOLUTE_CODEC, BEFORE_AFTER).xmap(f -> {
		if (f.left().isPresent()) return f.left().get();
		if (f.right().isPresent()) return f.right().get();
		return null;
	}, f -> {
		if (f instanceof SortingOrder.Absolute<ResourceLocation> absolute) return Either.left(absolute);
		if (f instanceof SortingOrder.After<ResourceLocation> || f instanceof SortingOrder.Before<ResourceLocation>) return Either.right(f);
		return null;
	});
}
