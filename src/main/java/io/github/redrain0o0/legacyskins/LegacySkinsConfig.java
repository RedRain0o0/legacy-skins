package io.github.redrain0o0.legacyskins;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class LegacySkinsConfig {
	public static final Codec<LegacySkinsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(SkinReference.CODEC.optionalFieldOf("skin").forGetter(a -> a.skin)).apply(instance, LegacySkinsConfig::new));
	// selected skin
	public Optional<SkinReference> skin;

	public LegacySkinsConfig(Optional<SkinReference> skin) {
		this.skin = skin;
	}

	public void setSkin(@Nullable SkinReference skin) {
		this.skin = Optional.ofNullable(skin);
		LegacySkinUtils.switchSkin(skin != null ? LegacySkinPack.list.get(skin.pack()).skins().get(skin.ordinal()) : null);
	}

	public void save() {
		Path configFile = FabricLoader.getInstance().getConfigDir().resolve("legacyskins.json");
		Optional<JsonElement> element = LegacySkinsConfig.CODEC.encodeStart(JsonOps.INSTANCE, this).resultOrPartial(Legacyskins.LOGGER::error);
		if (element.isEmpty()) throw new RuntimeException("Config not serialized!");
		try {
			Files.writeString(configFile, new GsonBuilder().setPrettyPrinting().create().toJson(element.get()));
		} catch (IOException e) {
			Legacyskins.LOGGER.error("Failed to save config", e);
		}
	}
}
