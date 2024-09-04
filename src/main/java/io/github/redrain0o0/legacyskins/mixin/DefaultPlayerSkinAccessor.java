package io.github.redrain0o0.legacyskins.mixin;

import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DefaultPlayerSkin.class)
public interface DefaultPlayerSkinAccessor {
	@Accessor("DEFAULT_SKINS")
	static PlayerSkin[] getDefaultSkins() {
		throw new AssertionError();
	}
}
