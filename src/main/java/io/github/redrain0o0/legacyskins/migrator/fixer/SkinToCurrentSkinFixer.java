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
		//? if >=1.20.6
		return element.renameField("skin", "currentSkin");
		// Old versions of DFU don't have renameField
		//? if <=1.20.4 {
		/*Dynamic<T> newDynamic = element.remove("skin");
		Optional<Dynamic<T>> skin = element.get("skin").result();
		skin.ifPresent(tDynamic -> newDynamic.set("currentSkin", tDynamic));
		return newDynamic;
		*///?}
	}
}
