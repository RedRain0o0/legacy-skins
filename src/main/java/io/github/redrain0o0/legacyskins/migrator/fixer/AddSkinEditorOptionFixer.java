package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

public class AddSkinEditorOptionFixer extends Fixer {

	public AddSkinEditorOptionFixer() {
		super(1003);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element.set("showSkinEditorButton", element.createBoolean(false));
	}
}
