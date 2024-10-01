package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JavaOps;
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;

import java.util.Optional;

// requires GameProfile not to be null
public class To1005Fixer extends Fixer {

	public To1005Fixer() {
		super(1005);
	}

	@Override
	public <T> Dynamic<T> fix(Dynamic<T> element) {
		Optional<Dynamic<T>> currentSkin = element.get("currentSkin").result();
		Optional<Dynamic<T>> favorites = element.get("favorites").result();
		// Remove currentSkin and favorites
		if (currentSkin.isPresent()) {
			element = element.remove("currentSkin");
		}
		if (favorites.isPresent()) {
			element = element.remove("favorites");
		}
		// Add skin and favorites to the profile with the current active uuid
		Dynamic<T> skinConfig = element.emptyMap();
		Dynamic<T> profile = element.emptyMap();
		if (currentSkin.isPresent()) {
			profile = profile.set("selectedSkin", currentSkin.get());
		}
		if (favorites.isPresent()) {
			profile = profile.set("favorites", favorites.get());
		} else {
			profile = profile.set("favorites", profile.emptyList());
		}
		skinConfig = skinConfig.set((String) /* If this is not a string we're in big trouble */ UUIDUtil.STRING_CODEC.encodeStart(JavaOps.INSTANCE, Minecraft.getInstance().getGameProfile().getId()).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow(), profile);
		element = element.set("profiles", skinConfig);
		return element;
	}
}
