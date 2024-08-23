package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

// showDevPacks will now always appear in the config file, even when set to false
public class To1004Fixer extends Fixer {
	public To1004Fixer() {
		super(1004);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		if (element.getElement("showDevPacks").resultOrPartial().isEmpty()) {
			element = element.set("showDevPacks", element.createBoolean(false));
		}
		return element;
	}
}