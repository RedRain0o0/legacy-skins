package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

public abstract class Fixer {
	public final int minApplicable;

	/**
	 * @param minApplicable The minimum applicable schema version to apple the fix to.
	 */
	public Fixer(int minApplicable) {
		this.minApplicable = minApplicable;
	}

	/**
	 * @param element The input {@link Dynamic<T>}
	 * @return The fixed {@link Dynamic<T>}
	 * @param T The {@link Dynamic<T>}'s underlying value's type.
	 */
	@SuppressWarnings("JavadocReference") // IntelliJ bug, where it doesn't display Javadoc correctly
	public abstract <T> Dynamic<T> fix(Dynamic<T> element);
}
