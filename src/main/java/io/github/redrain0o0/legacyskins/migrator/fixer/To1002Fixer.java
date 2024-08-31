package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

// Set skins skin type to "default", and rename the "skin" field to "currentSkin"
public class To1002Fixer extends Fixer {
	public To1002Fixer() {
		super(1002);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		Dynamic<?> defaultOption = element.createString("default");
		return renameField(element.set("skinsScreen", defaultOption), "skin", "currentSkin");
	}
}
