package io.github.redrain0o0.legacyskins.migrator.fixer.skinpacks;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import io.github.redrain0o0.legacyskins.migrator.fixer.Fixer;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

public class To2Fixer extends Fixer {

	public To2Fixer() {
		super(2);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		Dynamic<T> finalElement = element;
		element = element.updateMapValues(dynamicDynamicPair -> {
			Dynamic<T> key = (Dynamic<T>) dynamicDynamicPair.getFirst();
			Dynamic<T> second = (Dynamic<T>) dynamicDynamicPair.getSecond();
			if (second.getMapValues().error().isPresent()) return dynamicDynamicPair; // don't bother if we can't read the map;

			Optional<? extends Dynamic<T>> skins = second.get("skins").result();
			if (skins.isEmpty()) {
				return dynamicDynamicPair;
			} else {
				Dynamic<T> dynamic = skins.get();
				// java does not like the type of this
				var streamOpt = dynamic.asStreamOpt();
				if (streamOpt.error().isPresent()) return dynamicDynamicPair;
				Stream<? extends Dynamic<T>> orThrow = streamOpt.result().orElseThrow();
				ArrayList<Dynamic<T>> objects = new ArrayList<>();
				orThrow.forEachOrdered(dynamic1 -> {
					if (dynamic1.getMapValues().error().isPresent()) {
						objects.add(dynamic1);
					} else {
						Dynamic<T> set = dynamic1.set("modelType", dynamic1.createString("cpm"));
						objects.add(set);
					}
				});
				dynamic = finalElement.createList(objects.stream());
				second = second.set("skins", dynamic);
				return Pair.of(key, second);
			}
		});
		return element;
	}
}
