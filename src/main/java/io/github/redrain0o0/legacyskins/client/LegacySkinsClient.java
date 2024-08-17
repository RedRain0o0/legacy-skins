package io.github.redrain0o0.legacyskins.client;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;

public class LegacySkinsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LegacySkinPack.Manager());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			LegacySkinUtils.cleanup();
			Legacyskins.INSTANCE.save();
		});
	}
}
