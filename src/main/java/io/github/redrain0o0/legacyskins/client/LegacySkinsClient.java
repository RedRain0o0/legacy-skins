package io.github.redrain0o0.legacyskins.client;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.screen.config.LegacyConfigScreens;
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
//? if >=1.20.6 {
import net.neoforged.fml.common.EventBusSubscriber;
//?} else {
/^import net.neoforged.fml.common.Mod.EventBusSubscriber;
^///?}
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
*///?}

//? if neoforge && multientrypoints
/*@Mod(value = Legacyskins.MOD_ID, dist = Dist.CLIENT)*/
//? if neoforge {
/*@EventBusSubscriber(/^? if !multientrypoints {^/ /^modid = Legacyskins.MOD_ID, ^//^?}^/ bus = EventBusSubscriber.Bus.MOD)
*///?}
public class LegacySkinsClient {
	public void onInitializeClient() {
		//? if fabric {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LegacySkinPack.Manager());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			LegacySkinUtils.cleanup();
			Legacyskins.INSTANCE.save();
		});
		LegacyConfigScreens.init();
		//?}
	}

	//? if neoforge {
	/*public LegacySkinsClient() {
		NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, LegacySkinsClient::event);
		LegacyConfigScreens.init();
	}

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
