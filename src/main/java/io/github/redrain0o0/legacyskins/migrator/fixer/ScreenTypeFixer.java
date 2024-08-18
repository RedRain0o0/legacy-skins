package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

public class ScreenTypeFixer extends Fixer {

	public ScreenTypeFixer() {
		super(1001);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		Dynamic<?> defaultOption = element.createString("default");
		return element.set("skinsScreen", defaultOption);
	}
}
