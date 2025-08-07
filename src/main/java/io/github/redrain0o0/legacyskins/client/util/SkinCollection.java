package io.github.redrain0o0.legacyskins.client.util;

import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacyPackType;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Skin packs contain {@link io.github.redrain0o0.legacyskins.client.LegacySkin}s, while skin collections contain {@link SkinReference}s.<br>
 * A {@link SkinReference} is a reference to a {@link io.github.redrain0o0.legacyskins.client.LegacySkin} within a skin pack.
 */
public class SkinCollection {
	private ArrayList<SkinReference> backingArray;
	private final Supplier<ArrayList<SkinReference>> supplier;
	private final Object backing;
	private SkinCollection(Supplier<ArrayList<SkinReference>> arr, Object backing) {
		this.supplier = arr;
		this.backingArray = arr.get();
		this.backing = backing;
	}
	public static SkinCollection ofSkinPack(LegacySkinPack pack) {
		if (LegacySkinUtils.id(pack).equals(Constants.FAVORITES_PACK)) return ofFavorites();
		if (LegacySkinUtils.id(pack).equals(Constants.CREDITORS_PACK)) return ofCreditors();
		return new SkinCollection(() -> LegacySkinUtils.referencesFromSkinPack(pack), pack);
	}

	public static SkinCollection ofSkinPack(ResourceLocation id) {
		if (id == null) id = Constants.DEFAULT_PACK;
		return ofSkinPack(LegacySkinPack.list.get(id));
	}

	public static SkinCollection ofFavorites() {
		return new SkinCollection(LegacySkins.lazyInstance().getActiveSkinsConfig()::getFavorites, LegacySkinPack.list.get(Constants.FAVORITES_PACK));
	}

	/*
	 * Gets all skins from all dev packs, excluding the "legacyskins:creditors" and "legacyskins:modern-defaults" packs.
	 */
	public static SkinCollection ofCreditors() {
		return new SkinCollection(() -> new ArrayList<>(LegacySkinPack.list.entrySet().stream().filter(legacySkinPack -> legacySkinPack.getValue().type() == LegacyPackType.DEV && !Constants.CREDITORS_PACK.equals(legacySkinPack.getKey()) && !Constants.MODERN_DEFAULTS_PACK.equals(legacySkinPack.getKey())).map(Map.Entry::getValue).map(LegacySkinUtils::referencesFromSkinPack).flatMap(Collection::stream).toList()), LegacySkinPack.list.get(Constants.CREDITORS_PACK));
	}

	public SkinCollection refresh() {
		this.backingArray = supplier.get();
		return this;
	}

	public ResourceLocation icon() {
		if (backing instanceof LegacySkinPack pack) {
			return pack.icon();
		}
		return Constants.MISSING_TEXTURE;
	}

	public LegacyPackType type() {
		if (backing instanceof LegacySkinPack pack) {
			return pack.type();
		}
		return LegacyPackType.DEFAULT;
	}

	public int indexOf(SkinReference reference) {
		return backingArray.indexOf(reference);
	}

	// plz don't modify this
	public ArrayList<SkinReference> skins() {
		return backingArray;
	}

	public boolean isEmpty() {
		return backingArray.isEmpty();
	}

	public boolean has(SkinReference reference) {
		return backingArray.contains(reference);
	}
}
