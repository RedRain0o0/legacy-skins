package io.github.redrain0o0.legacyskins.client.util;

import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.config.ConfigKeys;
import com.tom.cpm.shared.config.ModConfig;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LegacySkinUtils {
	public static void switchSkin(LegacySkin skin) {
		ResourceLocation texture = skin.texture();
		Resource resource = Minecraft.getInstance().getResourceManager().getResource(texture).orElseThrow();
		try (InputStream opened = resource.open()) {
			ModConfig.getCommonConfig().setString(ConfigKeys.SELECTED_MODEL, temp(skin.texture(), opened.readAllBytes()));
			ModConfig.getCommonConfig().save();
			MinecraftClientAccess.get().sendSkinUpdate();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String temp(ResourceLocation location, byte[] bytes) {
		Path gameDir = FabricLoader.getInstance().getGameDir();
		Path playerModels = gameDir.resolve("player_models");
		Path resolve = playerModels.resolve(Legacyskins.MOD_ID + "-models");
		Path resolve1 = resolve.resolve(location.hashCode() + ".cpmmodel");
		resolve.toFile().mkdirs();
		try {
			resolve1.toFile().createNewFile();
			Files.write(resolve1, bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return Legacyskins.MOD_ID + "-models/" + location.hashCode() + ".cpmmodel";
	}
}
