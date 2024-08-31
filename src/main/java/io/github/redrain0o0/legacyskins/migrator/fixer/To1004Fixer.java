package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

// Add showDevPacks to the config file if it is missing, since it now is mandatory in schema 1004
public class To1004Fixer extends Fixer {
	public To1004Fixer() {
		super(1004);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		if (element.getElement("showDevPacks").result().isEmpty()) {
			element = element.set("showDevPacks", element.createBoolean(false));
		}
		return element;
	}
}
