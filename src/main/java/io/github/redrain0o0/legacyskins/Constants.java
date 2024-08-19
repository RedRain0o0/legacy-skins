package io.github.redrain0o0.legacyskins;

import io.github.redrain0o0.legacyskins.client.LegacySkin;
import net.minecraft.resources.ResourceLocation;

public enum Constants {;
	public static final LegacySkin FALLBACK_SKIN = new LegacySkin(ResourceLocation.fromNamespaceAndPath("legacyskins", "fallback.cpmmodel"));
	public static final ResourceLocation DEFAULT_PACK = ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "default");
	public static final ResourceLocation FAVORITES_PACK = ResourceLocation.fromNamespaceAndPath(Legacyskins.MOD_ID, "favorites");
}
