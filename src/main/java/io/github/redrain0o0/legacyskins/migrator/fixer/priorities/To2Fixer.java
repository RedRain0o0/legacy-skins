package io.github.redrain0o0.legacyskins.migrator.fixer.priorities;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import io.github.redrain0o0.legacyskins.migrator.fixer.Fixer;
import net.minecraft.resources.ResourceLocation;

public class To2Fixer extends Fixer {

	public To2Fixer() {
		super(2);
	}

	private static final UnboundedMapCodec<ResourceLocation, Double> OLD_PRIORITIES_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE);
	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		if (OLD_PRIORITIES_CODEC.decode(element).error().isPresent()) throw new IllegalStateException();
		return element;
	}
}
