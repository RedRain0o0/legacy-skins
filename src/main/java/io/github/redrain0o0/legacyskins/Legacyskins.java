package io.github.redrain0o0.legacyskins;


import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//? if neoforge {
/*import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.InterModComms;
import net.neoforged.fml.event.lifecycle.InterModEnqueueEvent;
*///?}

import java.util.function.Supplier;

//? if neoforge
/*@Mod(Legacyskins.MOD_ID)*/
public class Legacyskins {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "legacyskins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static LegacySkinsConfig INSTANCE;

	public static LegacySkinsConfig lazyInstance() {
		if (INSTANCE == null) LegacySkinsConfig.load();
		return INSTANCE;
	}

	//? if neoforge {
	/*public Legacyskins(/^? if constructorargs {^/ IEventBus bus, ModContainer container /^?}^/) {
		onInitialize();
		bus.addListener(InterModEnqueueEvent.class, this::event);
		//? if !multientrypoints
		PlatformUtils.executeInDist(PlatformUtils.Env.CLIENT, () -> () -> () -> new LegacySkinsClient(container));
	}


	private void event(InterModEnqueueEvent event) {
		InterModComms.sendTo("cpm", "api", () -> (Supplier<?>) () -> new CPMCompat());
	}
	*///?}

	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		//noinspection SpellCheckingInspection
		/* TODO ??? */LOGGER.debug("Loaded Legacy Skins's config.");
	}
}