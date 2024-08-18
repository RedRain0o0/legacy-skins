package io.github.redrain0o0.legacyskins.schema;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.schema.fixer.Fixer;
import io.github.redrain0o0.legacyskins.schema.fixer.ScreenTypeFixer;
import io.github.redrain0o0.legacyskins.schema.fixer.SkinToCurrentSkinFixer;

import java.util.List;

public class LegacySkinsDataFixer {
	public static final int OLDEST_SUPPORTED_VERSION = 1001;
	public static final int SCHEMA_VERSION = 1002;
	private final List<Fixer> fixers;
	public static final LegacySkinsDataFixer CONFIG_FIXER = new LegacySkinsDataFixer(
			List.of(
					new ScreenTypeFixer(),
					new SkinToCurrentSkinFixer()
			)
	);

	public LegacySkinsDataFixer(List<Fixer> fixers) {
		this.fixers = fixers;
	}

	public <T> Dynamic<T> fix(Dynamic<T> element) {
		int schemaVersion = element.get("schemaVersion").asInt(1001);
		if (schemaVersion < OLDEST_SUPPORTED_VERSION) throw new UnsupportedOperationException();
		element.remove("schemaVersion");
		if (schemaVersion > SCHEMA_VERSION) throw new UnsupportedOperationException();
		if (schemaVersion == SCHEMA_VERSION) return element;
		Dynamic<T> dynamic = element;
		for (Fixer fixer : fixers) {
			if (fixer.minApplicable >= schemaVersion) {
				dynamic = fixer.fix(dynamic);
			}
		}
		return dynamic;
	}

	public <T> Dynamic<T> addSchemaVersion(Dynamic<T> dynamic) {
		DynamicOps<T> ops = dynamic.getOps();
		dynamic = dynamic.set("schemaVersion", dynamic.createInt(LegacySkinsDataFixer.SCHEMA_VERSION));
		MapLike<T> map = ops.getMap(dynamic.getValue()).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		T t = ops.mergeToMap(dynamic.getValue(), map).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		return new Dynamic<>(ops, t);
	}
}
