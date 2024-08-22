package io.github.redrain0o0.legacyskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import io.github.redrain0o0.legacyskins.util.SkinTextureToCustomPlayerModel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;

public class Legacyskins implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "legacyskins";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static LegacySkinsConfig INSTANCE;
	static {
		// Load earlier, so nothing bad happens if CPM loads earlier.
		LegacySkinsConfig.load();
	}
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		//noinspection SpellCheckingInspection
		LOGGER.debug("Loaded Legacy Skins's config.");
	}
}