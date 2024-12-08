package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

// Nothing to do here, just a new value "nonLegacy4J" is available in the config file.
public class To1006Fixer extends Fixer {

	public To1006Fixer() {
		super(1006);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element;
	}
}
