package io.github.redrain0o0.legacyskins.schema.fixer;

import com.mojang.serialization.Dynamic;

public abstract class Fixer {
	public final int minApplicable;

	public Fixer(int minApplicable) {
		this.minApplicable = minApplicable;
	}

	public abstract <T> Dynamic<T> fix(Dynamic<T> element);
}
