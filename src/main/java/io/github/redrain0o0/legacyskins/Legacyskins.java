package io.github.redrain0o0.legacyskins;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Legacyskins implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "legacyskins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static LegacySkinsConfig INSTANCE;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");

		// TODO this is too complicated
		try {
			configLoad:
			{
				Path configFile = FabricLoader.getInstance().getConfigDir().resolve("legacyskins.json");
				configFromFile:
				{
					if (configFile.toFile().isFile()) {
						JsonElement s = new Gson().fromJson(Files.readString(configFile), JsonElement.class);
						Optional<LegacySkinsConfig> legacySkinsConfig = LegacySkinsConfig.CODEC.parse(JsonOps.INSTANCE, s).resultOrPartial(LOGGER::error);
						if (legacySkinsConfig.isEmpty()) break configFromFile;
						INSTANCE = legacySkinsConfig.get();
						break configLoad;
					}
				}
				LegacySkinsConfig config = new LegacySkinsConfig(Optional.empty());
				Optional<JsonElement> element = LegacySkinsConfig.CODEC.encodeStart(JsonOps.INSTANCE, config).resultOrPartial(LOGGER::error);
				if (element.isEmpty()) throw new RuntimeException("Config not serialized!");
				Files.writeString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(element.get()));
			}

		} catch (Throwable t) {
			LOGGER.error("Failed to load configs", t);
		}
		LOGGER.debug("Loaded Legacy Skins's config.");
	}
}