package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;

public class SkinToCurrentSkinFixer extends Fixer {

	public SkinToCurrentSkinFixer() {
		super(1002);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element.renameField("skin", "currentSkin");
	}
}
