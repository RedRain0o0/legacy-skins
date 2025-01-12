package io.github.redrain0o0.legacyskins.client;

import io.github.redrain0o0.legacyskins.LegacySkinsConfig;
import io.github.redrain0o0.legacyskins.Legacyskins;
import io.github.redrain0o0.legacyskins.client.screen.ChangeSkinScreen;
import io.github.redrain0o0.legacyskins.client.screen.EScreen;
import io.github.redrain0o0.legacyskins.client.screen.NonLegacy4JChangeSkinScreen;
import io.github.redrain0o0.legacyskins.client.screen.config.LegacyConfigScreens;
import io.github.redrain0o0.legacyskins.client.util.EasterEggUtils;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
//? if fabric {
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
//?}
import net.minecraft.client.Minecraft;
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
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
//?} else {
/^import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.neoforge.client.ConfigScreenHandler;
^///?}
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
*///?}
import java.util.function.Supplier;

//? if neoforge && multientrypoints
/*@Mod(value = Legacyskins.MOD_ID, dist = Dist.CLIENT)*/
//? if neoforge || forge {
/*@EventBusSubscriber(/^? if !multientrypoints {^/ /^modid = Legacyskins.MOD_ID, ^//^?}^/ bus = EventBusSubscriber.Bus.MOD)
*///?}
public class LegacySkinsClient {
	// used in a mixin
	public static boolean singleFireAssortApply = false;
	public void onInitializeClient() {
		//? if fabric {
		ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new LegacySkinPack.Manager());
		ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
			LegacySkinUtils.cleanup();
			Legacyskins.INSTANCE.save();
		});
		LegacyConfigScreens.init();
		//?}
		//? if legacy4j: >=1.7.5 {
		if (PlatformUtils.isModLoaded("legacy")) {
			Supplier<Supplier<Supplier<Runnable>>> goofyahhclassloading = new Supplier<Supplier<Supplier<Runnable>>>() {
				@Override
				public Supplier<Supplier<Runnable>> get() {
					return new Supplier<Supplier<Runnable>>() {
						@Override
						public Supplier<Runnable> get() {
							return new Supplier<Runnable>() {
								@Override
								public Runnable get() {
									return new Runnable() {
										@Override
										public void run() {
											wily.factoryapi.base.client.UIDefinition.Manager.WidgetAction.defaultScreensMap.put(io.github.redrain0o0.legacyskins.util.VersionUtils.of(Legacyskins.MOD_ID, "skins_screen"), LegacySkinsClient::getSkinsScreen);
										}
									};
								}
							};
						}
					};
				}
			};
			goofyahhclassloading.get().get().get().run();
		}
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
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, (Supplier<IConfigScreenFactory>) () -> (IConfigScreenFactory) (container, prev) -> LegacyConfigScreens.createConfigScreen(prev).orElseThrow());
			//?} else
			/^modContainer.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> LegacyConfigScreens.createConfigScreen(screen).orElseThrow()));^/
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


	// NeoForge 1.21.4 crashes if ChangeSkinsScreen is referenced in a return statement, so we try to isolate classloading as much as we can.
	public static Screen getSkinsScreen(Screen previousScreen) {
		Supplier<Supplier<Supplier<Supplier<Screen>>>> supplier = new Supplier<Supplier<Supplier<Supplier<Screen>>>>() {
			@Override
			public Supplier<Supplier<Supplier<Screen>>> get() {
				return new Supplier<Supplier<Supplier<Screen>>>() {
					@Override
					public Supplier<Supplier<Screen>> get() {
						return new Supplier<Supplier<Screen>>() {
							@Override
							public Supplier<Screen> get() {
								return new Supplier<Screen>() {
									@Override
									public Screen get() {
										return EasterEggUtils.eEasterEgg() ? new EScreen(previousScreen) : Legacyskins.INSTANCE.getSkinsScreen() == LegacySkinsConfig.SkinsScreen.DEFAULT || Legacyskins.INSTANCE.getSkinsScreen() == LegacySkinsConfig.SkinsScreen.REMOVED_CLASSIC ? new ChangeSkinScreen(previousScreen) : new NonLegacy4JChangeSkinScreen(previousScreen);
									}
								};
							}
						};
					}
				};
			}
		};
		return supplier.get().get().get().get();
	}
	public static void openScreen(Screen previousScreen) {
		Minecraft.getInstance().setScreen(getSkinsScreen(previousScreen));
	}
}
