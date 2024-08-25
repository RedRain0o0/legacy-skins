package io.github.redrain0o0.legacyskins;

import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.resources.ResourceLocation;

public enum Constants {;
	public static final LegacySkin FALLBACK_SKIN = new LegacySkin(VersionUtils.of("legacyskins", "fallback.cpmmodel"));
	public static final ResourceLocation DEFAULT_PACK = VersionUtils.of(Legacyskins.MOD_ID, "default");
	public static final ResourceLocation FAVORITES_PACK = VersionUtils.of(Legacyskins.MOD_ID, "favorites");
}
