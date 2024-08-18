package io.github.redrain0o0.legacyskins.schema;

import com.mojang.serialization.Dynamic;

import java.util.List;

public class LegacySkinsDataFixer {
	public static final int OLDEST_SUPPORTED_VERSION = 1001;
	public static final int SCHEMA_VERSION = 1002;
	public static <T> Dynamic<T> fix(Dynamic<T> element) {
		int schemaVersion = element.get("schemaVersion").asInt(1001);
		if (schemaVersion < OLDEST_SUPPORTED_VERSION) throw new UnsupportedOperationException();
		element.remove("schemaVersion");
		if (schemaVersion > SCHEMA_VERSION) throw new UnsupportedOperationException();
		if (schemaVersion == SCHEMA_VERSION) return element;
		Dynamic<T> dynamic = element;
		for (Fixer fixer : fixers()) {
			if (fixer.minApplicable >= schemaVersion) {
				dynamic = fixer.fix(dynamic);
			}
		}
		return dynamic;
	}

	public static List<Fixer> fixers() {
		return List.of(new ScreenTypeFixer(),new SkinToCurrentSkinFixer());
	}

	public static class ScreenTypeFixer extends Fixer {

		public ScreenTypeFixer() {
			super(1001);
		}

		@Override
		public <T> Dynamic<T> fix(Dynamic<T> element) {
			Dynamic<?> defaultOption = element.createString("default");
			return element.set("skinsScreen", defaultOption);
		}
	}

	public static class SkinToCurrentSkinFixer extends Fixer {

		public SkinToCurrentSkinFixer() {
			super(1001);
		}

		@Override
		public <T> Dynamic<T> fix(Dynamic<T> element) {
			return element.renameField("skin", "currentSkin");
		}
	}

	public static abstract class Fixer {
		private final int minApplicable;
		public Fixer(int minApplicable) {
			this.minApplicable = minApplicable;
		}
		public abstract <T> Dynamic<T> fix(Dynamic<T> element);
	}
}
