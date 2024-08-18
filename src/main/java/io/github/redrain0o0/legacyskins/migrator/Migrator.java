package io.github.redrain0o0.legacyskins.migrator;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.migrator.fixer.Fixer;
import io.github.redrain0o0.legacyskins.migrator.fixer.ScreenTypeFixer;
import io.github.redrain0o0.legacyskins.migrator.fixer.SkinToCurrentSkinFixer;

import java.util.List;

public class Migrator {
	private final List<Fixer> fixers;
	public static final Migrator CONFIG_FIXER = new Migrator(
			List.of(
					new ScreenTypeFixer(),
					new SkinToCurrentSkinFixer()
			), 1001, 1002
	);

	public static final Migrator SKIN_PACKS_FIXER = new Migrator(
			List.of(),
			1, 1
	);

	private final int oldestSupportedVersion;
	private final int schemaVersion;

	public Migrator(List<Fixer> fixers, int oldestSupportedVersion, int schemaVersion) {
		this.fixers = fixers;
		this.oldestSupportedVersion = oldestSupportedVersion;
		this.schemaVersion = schemaVersion;
	}

	public <T> Dynamic<T> fix(Dynamic<T> element) {
		int schemaVersion = element.get("schemaVersion").asInt(1001);
		if (schemaVersion < oldestSupportedVersion) throw new UnsupportedOperationException();
		element.remove("schemaVersion");
		if (schemaVersion > oldestSupportedVersion) throw new UnsupportedOperationException();
		if (schemaVersion == this.schemaVersion) return element;
		Dynamic<T> dynamic = element;
		for (Fixer fixer : fixers) {
			if (fixer.minApplicable >= schemaVersion) {
				dynamic = fixer.fix(dynamic);
			}
		}
		return dynamic;
	}

	public <T> T fix(DynamicOps<T> ops, T value) {
		return fix(new Dynamic<>(ops, value)).getValue();
	}

	public <T> Dynamic<T> addSchemaVersion(Dynamic<T> dynamic) {
		DynamicOps<T> ops = dynamic.getOps();
		Dynamic<T> newDynamic = new Dynamic<>(ops, ops.emptyMap());
		newDynamic = newDynamic.set("schemaVersion", dynamic.createInt(this.schemaVersion));
		MapLike<T> map = ops.getMap(dynamic.getValue()).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		T t = ops.mergeToMap(newDynamic.getValue(), map).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		return new Dynamic<>(ops, t);
	}

	public <T> T addSchemaVersion(DynamicOps<T> ops, T value) {
		return addSchemaVersion(new Dynamic<>(ops, value)).getValue();
	}
}
