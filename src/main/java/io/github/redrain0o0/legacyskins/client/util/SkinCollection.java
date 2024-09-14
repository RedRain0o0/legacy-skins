package io.github.redrain0o0.legacyskins.client.util;

import io.github.redrain0o0.legacyskins.Constants;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacyPackType;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.function.Supplier;

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
		return new SkinCollection(() -> LegacySkinUtils.referencesFromSkinPack(pack), pack);
	}

	public static SkinCollection ofSkinPack(ResourceLocation id) {
		if (id == null) id = Constants.DEFAULT_PACK;
		return ofSkinPack(LegacySkinPack.list.get(id));
	}

	public static SkinCollection ofFavorites() {
		return new SkinCollection(Legacyskins.lazyInstance()::getFavorites, LegacySkinPack.list.get(Constants.FAVORITES_PACK));
	}

	public SkinCollection refresh() {
		this.backingArray = supplier.get();
		return this;
	}

	public ResourceLocation icon() {
		if (backing instanceof LegacySkinPack pack) {
			return pack.icon();
		}
		return VersionUtils.of("minecraft", "missing"); // TODO proper missing texture
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
