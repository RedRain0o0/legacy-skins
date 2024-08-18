package io.github.redrain0o0.legacyskins.schema.fixer;

import com.mojang.serialization.Dynamic;

public class SkinToCurrentSkinFixer extends Fixer {

	public SkinToCurrentSkinFixer() {
		super(1001);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element.renameField("skin", "currentSkin");
	}
}
