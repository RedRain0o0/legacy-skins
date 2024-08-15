package io.github.redrain0o0.legacyskins.client;

import io.github.redrain0o0.legacyskins.Legacyskins;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;

public class LegacySkinsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LegacySkinPack.Manager());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			Path playerModels = FabricLoader.getInstance().getGameDir().resolve("player_models").resolve("legacyskins-models");
			try {
				FileUtils.delete(playerModels.toFile());
			} catch (IOException e) {
				// Don't bother throwing here, the client is stopping already
				Legacyskins.LOGGER.error("Failed to delete temporary models folder!", e);
			}
		});
	}
}
