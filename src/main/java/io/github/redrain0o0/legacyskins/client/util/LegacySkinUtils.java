package io.github.redrain0o0.legacyskins.client.util;

import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.config.ConfigKeys;
import com.tom.cpm.shared.config.ModConfig;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import io.github.redrain0o0.legacyskins.util.VersionUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class LegacySkinUtils {
	public static void switchSkin(@Nullable LegacySkin skin) {
		if (skin == null) {
			ModConfig.getCommonConfig().clearValue(ConfigKeys.SELECTED_MODEL);
			ModConfig.getCommonConfig().save();
		} else {
			try (InputStream opened = from(skin)) {
				ModConfig.getCommonConfig().setString(ConfigKeys.SELECTED_MODEL, temp(skin.model(), opened.readAllBytes()));
				ModConfig.getCommonConfig().save();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (Minecraft.getInstance().getConnection() != null) {
			MinecraftClientAccess.get().sendSkinUpdate();
		}
	}

	public static InputStream from(@NotNull LegacySkin skin) throws IOException {
		ResourceLocation texture = skin.model();
		try {
			Resource resource = Minecraft.getInstance().getResourceManager().getResource(texture).orElseThrow();
			return resource.open();
		} catch (Throwable t) {
			Legacyskins.LOGGER.error("Failed to load skin %s".formatted(texture), t);
			return from(new LegacySkin(VersionUtils.of("legacyskins", "fallback.cpmmodel")));
		}
	}

	public static void cleanup() {
		Path playerModels = PlatformUtils.getGameDir().resolve("player_models").resolve("legacyskins-models");
		try {
			FileUtils.deleteDirectory(playerModels.toFile());
		} catch (IOException e) {
			// Don't bother throwing here, the client is stopping already
			Legacyskins.LOGGER.error("Failed to delete temporary models folder!", e);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static String temp(ResourceLocation location, byte[] bytes) {
		Path gameDir = PlatformUtils.getGameDir();
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
