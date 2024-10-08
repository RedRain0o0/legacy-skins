package io.github.redrain0o0.legacyskins.client;

import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.screen.config.LegacyConfigScreens;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
//? if fabric {
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.packs.PackType;
//? if forge {
/*import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
*///?}
//? if neoforge {
/*import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
//? if >=1.20.6 {
/^import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
^///?} else {
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.ConfigScreenHandler;
//?}
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;

import java.util.function.Supplier;
*///?}

//? if neoforge && multientrypoints
/*@Mod(value = Legacyskins.MOD_ID, dist = Dist.CLIENT)*/
//? if neoforge || forge {
/*@EventBusSubscriber(/^? if !multientrypoints {^/ modid = Legacyskins.MOD_ID, /^?}^/ bus = EventBusSubscriber.Bus.MOD)
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

	//? if neoforge || forge {
	/*public LegacySkinsClient(ModContainer modContainer) {
		//? if neoforge
		NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, LegacySkinsClient::event);
		//? if forge
		/^MinecraftForge.EVENT_BUS.addListener(LegacySkinsClient::event);^/
		LegacyConfigScreens.init();
		if (LegacyConfigScreens.hasConfigScreens()) {
			//? if >=1.20.6 {
			/^modContainer.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) () -> (IConfigScreenFactory) (container, prev) -> LegacyConfigScreens.createConfigScreen(prev).orElseThrow());
			^///?} else
			modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> LegacyConfigScreens.createConfigScreen(screen).orElseThrow()));
		}
	}

	@SubscribeEvent
	public static void onResourceReload(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new LegacySkinPack.Manager());
	}

	public static void event(GameShuttingDownEvent event) {
		LegacySkinUtils.cleanup();
		Legacyskins.INSTANCE.save();
	}
	*///?}
}
