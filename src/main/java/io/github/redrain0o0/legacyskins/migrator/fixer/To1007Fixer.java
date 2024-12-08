package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import io.github.redrain0o0.legacyskins.Legacyskins;

// Replaces classic with removed_classic
public class To1007Fixer extends Fixer {

	public To1007Fixer() {
		super(1007);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		// "skinsScreen": "classic",
		OptionalDynamic<T> skinsScreen = element.get("skinsScreen");
		if ("classic".equals(skinsScreen.asString("default"))) {
			Legacyskins.LOGGER.info("Classic skins screen has temporarily been removed.");
			element = element.set("skinsScreen", element.createString("removed_classic"));
		}
		return element;
	}
}
