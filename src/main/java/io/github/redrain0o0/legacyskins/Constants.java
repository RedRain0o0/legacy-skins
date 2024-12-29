package io.github.redrain0o0.legacyskins;

import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.resources.ResourceLocation;

public enum Constants {;
	public static final LegacySkin FALLBACK_SKIN = new LegacySkin(VersionUtils.of("legacyskins", "fallback.cpmmodel"));
	public static final ResourceLocation DEFAULT_PACK = VersionUtils.of(Legacyskins.MOD_ID, "default");
	public static final ResourceLocation FAVORITES_PACK = VersionUtils.of(Legacyskins.MOD_ID, "favorites");
	public static final ResourceLocation CREDITORS_PACK = VersionUtils.of(Legacyskins.MOD_ID, "creditors");
	public static final ResourceLocation MODERN_DEFAULTS_PACK = VersionUtils.of(Legacyskins.MOD_ID, "modern-defaults");
	public static final ResourceLocation MISSING_TEXTURE = VersionUtils.of(Legacyskins.MOD_ID, "textures/skin_packs/missing.png");
	// Modrinth Logo is All Rights Reserved and used under fair use
	public static final ResourceLocation MODRINTH_STORE_LOGO = VersionUtils.of(Legacyskins.MOD_ID, "textures/branding/modrinth-store-logo.png");
}
