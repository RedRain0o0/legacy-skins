package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

// renames legacyskins:default to legacyskins:old-defaults
// TODO add this when we actually add the old packs
public class To1011Fixer extends Fixer {

	public To1011Fixer() {
		super(1011);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		Optional<Dynamic<T>> profiles = element.get("profiles").result();
		if (profiles.isEmpty()) return element;
		Optional<Map<Dynamic<T>, Dynamic<T>>> mapVals = profiles.get().getMapValues().result();
		if (mapVals.isEmpty()) return element;
		for (Map.Entry<Dynamic<T>, Dynamic<T>> entry : mapVals.get().entrySet()) {
			Dynamic<T> value = entry.getValue();
			Optional<String> string = entry.getKey().asString().result();
			if (string.isPresent()) {
				profiles = Optional.of(profiles.get().set(string.get(), patch(value)));
			}
		}

		return element.set("profiles", profiles.get());
	}

	private <T> Dynamic<T> patch(Dynamic<T> value) {
		Optional<Map<Dynamic<T>, Dynamic<T>>> result = value.getMapValues().result();
		if (result.isEmpty()) return value;
		Optional<Dynamic<T>> selectedSkin = value.get("selectedSkin").result();
		if (selectedSkin.isPresent()) {
			Dynamic<T> tDynamic = patchSkinPackName(selectedSkin.get());
			value = value.set("selectedSkin", tDynamic);
		}
		Optional<Dynamic<T>> favorites = value.get("favorites").result();
		if (favorites.isEmpty()) return value;
		Dynamic<T> tDynamic = favorites.get();
		Optional<Stream<Dynamic<T>>> result1 = tDynamic.asStreamOpt().result();
		if (result1.isEmpty()) return value;
		Stream<Dynamic<T>> dynamicStream = result1.get();
		Dynamic<T>[] tDynamic1 = new Dynamic[]{value.emptyList()};
		dynamicStream.forEachOrdered(tDynamic2 -> tDynamic1[0].merge(patchSkinPackName(tDynamic2)).result().ifPresent(a -> tDynamic1[0] = a));
		return value.set("favorites", tDynamic1[0]);
	}

	private <T> Dynamic<T> patchSkinPackName(Dynamic<T> t) {
		Dynamic<T> t1 = t;
		if (t.getMapValues().result().isEmpty()) return t;
		Optional<Dynamic<T>> pack = t.get("pack").result();
		if (pack.isEmpty()) return t;
		Optional<String> result = pack.get().asString().result();
		if (result.isEmpty()) return t;
		if ("legacyskins:default".equals(result.get())) {
			t = t.set("pack", t.createString("legacyskins:old-defaults"));
			OptionalDynamic<T> ordinal = t.get("ordinal");
			if (ordinal.result().isEmpty()) return t;
			Optional<Number> result1 = ordinal.asNumber().result();
			if (result1.isEmpty()) return t;
			int b = result1.get().intValue();
			if (b == 0) return t1;
			t = t.set("ordinal", t.createInt(b-1));
		}
		return t;
	}
}
