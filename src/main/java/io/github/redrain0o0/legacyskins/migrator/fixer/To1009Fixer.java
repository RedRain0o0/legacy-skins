package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

// Add erase modrinth authentication, as we have to sign in again.
public class To1009Fixer extends Fixer {

	public To1009Fixer() {
		super(1009);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element.set("modrinthAuthentication", element.emptyMap());
	}
}
