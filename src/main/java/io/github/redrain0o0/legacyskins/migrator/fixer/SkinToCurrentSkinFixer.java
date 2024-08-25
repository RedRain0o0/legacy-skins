package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;

import java.util.Optional;
import java.util.function.UnaryOperator;

public class SkinToCurrentSkinFixer extends Fixer {

	public SkinToCurrentSkinFixer() {
		super(1002);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return renameField(element, "skin", "currentSkin");
	}
}
