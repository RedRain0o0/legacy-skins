package io.github.redrain0o0.legacyskins.migrator.fixer;

import com.mojang.serialization.Dynamic;
//? if <=1.20.4 && >1.20.2 {
/*import net.minecraft.util.JavaOps;
*///?} elif >1.20.4 {
import com.mojang.serialization.JavaOps;
//?} else
/*import com.mojang.serialization.JsonOps;*/
import io.github.redrain0o0.legacyskins.Legacyskins;
import net.minecraft.client.Minecraft;
import net.minecraft.core.UUIDUtil;

import java.util.Optional;
import java.util.UUID;

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
		UUID uuid;
		try {
			uuid = Minecraft.getInstance()/*? if <=1.20.1 {*//*.getUser()*//*?}*/.getGameProfile().getId();
		} catch (Throwable t) {
			uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
			if (!System.getProperties().containsKey("legacy-skins-unit-testing")) Legacyskins.LOGGER.error("Failed to fetch player uuid!", t);
		}
		skinConfig = skinConfig.set((String) /* If this is not a string we're in big trouble */ UUIDUtil.STRING_CODEC.encodeStart(/*? if >=1.20.4 {*/JavaOps/*?} else {*//*JsonOps*//*?}*/.INSTANCE, uuid).resultOrPartial(Legacyskins.LOGGER::error).orElseThrow()/*? if <1.20.4 {*//*.getAsString()*//*?}*/, profile);
		element = element.set("profiles", skinConfig);
		return element;
	}
}
