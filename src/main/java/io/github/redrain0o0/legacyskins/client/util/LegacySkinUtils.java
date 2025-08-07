package io.github.redrain0o0.legacyskins.client.util;

import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.SkinReference;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LegacySkinUtils {
	public static void switchSkin(@Nullable LegacySkin skin) {
		if (skin != null && skin.type() == LegacySkin.Type.FIGURA) {
			LegacySkinsClient.generalCpmQ.vh1();
			LegacySkinsClient.generalFiguraQ.q5();
			LegacySkinsClient.generalFiguraQ.q4(() -> LegacySkinsClient.generalFiguraQ.q(skin));

			if (Minecraft.getInstance().getConnection() != null) {
				LegacySkinsClient.generalCpmQ.vh3();
			}
			return;
		}
		if (skin == null) {
			LegacySkinsClient.generalCpmQ.vh1();
			LegacySkinsClient.generalFiguraQ.q5();
			LegacySkinsClient.generalFiguraQ.q4(() -> LegacySkinsClient.generalFiguraQ.q2());
		} else {
			try (InputStream opened = from(skin)) {
				LegacySkinsClient.generalCpmQ.vh2(skin, opened);
				LegacySkinsClient.generalFiguraQ.q5();
				LegacySkinsClient.generalFiguraQ.q4(() -> LegacySkinsClient.generalFiguraQ.q2());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (Minecraft.getInstance().getConnection() != null) {
			LegacySkinsClient.generalCpmQ.vh3();
		}
	}

	public static InputStream from(@NotNull LegacySkin skin) throws IOException {
		ResourceLocation texture = skin.model();
		try {
			Resource resource = Minecraft.getInstance().getResourceManager().getResource(texture).orElseThrow();
			return resource.open();
		} catch (Throwable t) {
			LegacySkins.LOGGER.error("Failed to load skin %s".formatted(texture), t);
			return from(new LegacySkin(VersionUtils.of("legacyskins", "fallback.cpmmodel")));
		}
	}

	public static void cleanup() {
		Path playerModels = PlatformUtils.getGameDir().resolve("player_models").resolve("legacyskins-models");
		Path lstempcache = PlatformUtils.getGameDir().resolve(".ls-tempcache");
		try {
			FileUtils.deleteDirectory(lstempcache.toFile());
			FileUtils.deleteDirectory(playerModels.toFile());
		} catch (IOException e) {
			// Don't bother throwing here, the client is stopping already
			LegacySkins.LOGGER.error("Failed to delete temporary models folder!", e);
		}
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public static String temp(ResourceLocation location, byte[] bytes) {
		Path gameDir = PlatformUtils.getGameDir();
		Path playerModels = gameDir.resolve("player_models");
		Path resolve = playerModels.resolve(LegacySkins.MOD_ID + "-models");
		Path resolve1 = resolve.resolve(location.hashCode() + ".cpmmodel");
		resolve.toFile().mkdirs();
		try {
			resolve1.toFile().createNewFile();
			Files.write(resolve1, bytes);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return LegacySkins.MOD_ID + "-models/" + location.hashCode() + ".cpmmodel";
	}

	/**
	 * Gets skin references from a skin pack. Does not work with the favorites pack and the creditors pack.
	 * @see SkinCollection#ofSkinPack(LegacySkinPack)
	 */
	public static ArrayList<SkinReference> referencesFromSkinPack(LegacySkinPack pack) {
		List<LegacySkin> skins = pack.skins();
		ResourceLocation id = id(pack);
		ArrayList<SkinReference> references = new ArrayList<>();
		for (int i = 0; i < skins.size(); i++) {
			references.add(new SkinReference(id, i));
		}
		return references;
	}

	public static ResourceLocation id(LegacySkinPack pack) {
		return LegacySkinPack.list.entrySet().stream().filter(a -> a.getValue().equals(pack)).map(Map.Entry::getKey).findFirst().orElseThrow();
	}
}
