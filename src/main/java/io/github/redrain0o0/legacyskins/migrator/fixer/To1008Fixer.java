package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import io.github.redrain0o0.legacyskins.migrator.Migrator;

// Add downloadedPacks
public class To1008Fixer extends Fixer {

	public To1008Fixer() {
		super(1008);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		return element.set("downloadedPacks", element.emptyMap());
	}
}
