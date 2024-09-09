package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;
//? if <1.20.6
import java.util.Optional;

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

	public static <T> Dynamic<T> renameField(Dynamic<T> dynamic, String oldName, String newName) {
		//? if >=1.20.6 {
		/*return dynamic.renameField(oldName, newName);
		*///?} else {
		Dynamic<T> newDynamic = dynamic.remove(oldName);
		Optional<Dynamic<T>> value = dynamic.get(oldName).result();
		if (value.isPresent()) {
			newDynamic = newDynamic.set(newName, value.get());
		}
		return newDynamic;
		//?}
	}
}
