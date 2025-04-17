package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

// Add erase modrinth authentication, as we have to sign in again.
public class To1010Fixer extends Fixer {

	public To1010Fixer() {
		super(1010);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element.set("choppyLerp", element.createBoolean(false));
	}
}
