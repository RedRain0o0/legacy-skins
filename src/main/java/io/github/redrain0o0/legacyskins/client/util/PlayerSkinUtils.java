package io.github.redrain0o0.legacyskins.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
//? if <=1.20.1 {
/*import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
*///?} else
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PlayerSkinUtils {
	private static Map<GameProfile, F> info = new HashMap<>();

	public static F skinOf(GameProfile profile) {
		F f = of(profile);
		//? if <=1.20.1 {
		/*Minecraft.getInstance().getSkinManager().registerSkins(profile, f::onSkinTextureAvailable, false);
		*///?} else
		Minecraft.getInstance().getSkinManager().getOrLoad(profile).thenAcceptAsync(f::apply);
		return f;
	}

	private static F of(GameProfile gameProfile) {
		return info.computeIfAbsent(gameProfile, F::new);
	}

	public static class F {
		private final GameProfile profile;
		public ResourceLocation skinLocation;
		public boolean slim;
		// TODO implement capes
		private ResourceLocation capeLocation;
		public F(GameProfile profile) {
			this.profile = profile;
			//? if <=1.20.1 {
			/*this.skinLocation = Minecraft.getInstance().getSkinManager().getInsecureSkinLocation(profile);
			this.slim = "slim".equals(DefaultPlayerSkin.getSkinModelName(profile.getId()));
			this.skinLocation = DefaultPlayerSkin.getDefaultSkin(profile.getId());
			*///?} else {
			PlayerSkin insecureSkin = Minecraft.getInstance().getSkinManager().getInsecureSkin(profile);
			this.skinLocation = insecureSkin.texture();
			this.slim = "slim".equals(insecureSkin.model().id());
			//?}
		}

		private void onSkinTextureAvailable(MinecraftProfileTexture.Type type, ResourceLocation resourceLocation, MinecraftProfileTexture minecraftProfileTexture) {
			if (type == MinecraftProfileTexture.Type.SKIN) {
				this.skinLocation = resourceLocation;
				this.slim = "slim".equals(minecraftProfileTexture.getMetadata("model"));
			}
		}

		//? if >=1.20.2 {
		public void apply(PlayerSkin playerSkin) {
			this.skinLocation = playerSkin.texture();
			this.slim = "slim".equals(playerSkin.model().id());
		}
		//?}
	}
}
