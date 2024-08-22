package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

public abstract class Fixer {
	public final int maxApplicable;

	/**
	 * @param maxApplicable The maximum applicable schema version (-1) to apply the fix to.
	 */
	public Fixer(int maxApplicable) {
		this.maxApplicable = maxApplicable;
	}

	/**
	 * @param element The input {@link Dynamic<T>}
	 * @return The fixed {@link Dynamic<T>}
	 * @param <T> The {@link Dynamic<T>}'s underlying value's type.
	 */
	public abstract <T> Dynamic<T> fix(Dynamic<T> element);
}
