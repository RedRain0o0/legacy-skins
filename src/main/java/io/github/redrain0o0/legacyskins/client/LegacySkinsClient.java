package io.github.redrain0o0.legacyskins.client;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//?}
import net.minecraft.server.packs.PackType;
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
*///?}

//? if neoforge
/*@Mod(value = Legacyskins.MOD_ID, dist = Dist.CLIENT)*/
//? if neoforge
/*@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)*/
public class LegacySkinsClient {
	public void onInitializeClient() {
		//? if fabric {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LegacySkinPack.Manager());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			LegacySkinUtils.cleanup();
			Legacyskins.INSTANCE.save();
		});
		//?}
	}

	//? if neoforge {
	/*public LegacySkinsClient() {
		NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, LegacySkinsClient::event);
	}
	//?

	@SubscribeEvent
	public static void event(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new LegacySkinPack.Manager());
	}

	public static void event(GameShuttingDownEvent event) {
		LegacySkinUtils.cleanup();
		Legacyskins.INSTANCE.save();
	}
	*///?}
}
