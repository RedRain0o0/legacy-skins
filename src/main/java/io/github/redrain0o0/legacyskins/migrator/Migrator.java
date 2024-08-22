package io.github.redrain0o0.legacyskins.migrator;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.migrator.fixer.AddSkinEditorOptionFixer;
import io.github.redrain0o0.legacyskins.migrator.fixer.Fixer;
import io.github.redrain0o0.legacyskins.migrator.fixer.SkinsScreenFixer;
import io.github.redrain0o0.legacyskins.migrator.fixer.SkinToCurrentSkinFixer;

import java.util.List;

public class Migrator {
	private final List<Fixer> fixers;
	public static final Migrator CONFIG_FIXER = new Migrator(
			List.of(
					new SkinsScreenFixer(),
					new SkinToCurrentSkinFixer(),
					new AddSkinEditorOptionFixer()
			),
			1001,
			1001,
			1003
	);

	public static final Migrator SKIN_PACKS_FIXER = new Migrator(
			List.of(),
			1,
			1,
			1
	);

	private final int oldestSupportedVersion;
	private final int schemaVersion;
	private final int defaultValue;

	/**
	 *
	 * @param fixers A list of {@link Fixer}s
	 * @param defaultValue The default schema value, if it is not set
	 * @param oldestSupportedVersion The oldest supported schema version
	 * @param schemaVersion The current schema version
	 */
	public Migrator(List<Fixer> fixers, int defaultValue, int oldestSupportedVersion, int schemaVersion) {
		this.fixers = fixers;
		this.defaultValue = defaultValue;
		this.oldestSupportedVersion = oldestSupportedVersion;
		this.schemaVersion = schemaVersion;
	}

	/**
	 * @param element The input {@link Dynamic<T>}
	 * @return The fixed {@link Dynamic<T>}
	 * @param <T> The {@link Dynamic<T>}'s underlying value's type.
	 */
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		int schemaVersion = element.get("schemaVersion").asInt(defaultValue);
		if (schemaVersion < oldestSupportedVersion) throw new UnsupportedOperationException();
		element = element.remove("schemaVersion");
		if (schemaVersion > this.schemaVersion) throw new UnsupportedOperationException();
		if (schemaVersion == this.schemaVersion) return element;
		Dynamic<T> dynamic = element;
		for (Fixer fixer : fixers) {
			System.out.println(fixer.maxApplicable);
			if (fixer.maxApplicable > schemaVersion) {
				dynamic = fixer.fix(dynamic);
			}
		}
		return dynamic;
	}

	/**
	 * @param ops {@link DynamicOps<T>}
	 * @param value The input {@link T}
	 * @return A fixed {@link T}.
	 * @param <T> the input type of the {@code value}
	 */
	public <T> T fix(DynamicOps<T> ops, T value) {
		return fix(new Dynamic<>(ops, value)).getValue();
	}

	/**
	 * @param dynamic The input {@link Dynamic<T>}
	 * @return A {@link Dynamic<T>} that has a {@code schemaVersion} key added to it.
	 * @param <T> The {@link Dynamic<T>}'s underlying value's type.
	 */
	public <T> Dynamic<T> addSchemaVersion(Dynamic<T> dynamic) {
		DynamicOps<T> ops = dynamic.getOps();
		Dynamic<T> newDynamic = new Dynamic<>(ops, ops.emptyMap());
		newDynamic = newDynamic.set("schemaVersion", dynamic.createInt(this.schemaVersion));
		MapLike<T> map = ops.getMap(dynamic.getValue()).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		T t = ops.mergeToMap(newDynamic.getValue(), map).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow();
		return new Dynamic<>(ops, t);
	}

	/**
	 * @param ops {@link DynamicOps<T>}
	 * @param value The input {@link T}
	 * @return A {@link T} that has a {@code schemaVersion} key added to it.
	 * @param <T> the input type of the {@code value}
	 */
	public <T> T addSchemaVersion(DynamicOps<T> ops, T value) {
		return addSchemaVersion(new Dynamic<>(ops, value)).getValue();
	}
}
