package io.github.redrain0o0.legacyskins.client.screen;

import com.tom.cpm.shared.MinecraftClientAccess;
import com.tom.cpm.shared.config.ConfigKeys;
import com.tom.cpm.shared.config.ModConfig;
import io.github.redrain0o0.legacyskins.client.LegacySkin;

import java.io.IOException;
import java.io.InputStream;

import static io.github.redrain0o0.legacyskins.client.util.LegacySkinUtils.temp;

public class GeneralCPMQ implements IGeneralCPMQ {
	@Override
	public void vh1() {
		ModConfig.getCommonConfig().clearValue(ConfigKeys.SELECTED_MODEL);
		ModConfig.getCommonConfig().save();
	}

	@Override
	public void vh2(LegacySkin skin, InputStream opened) throws IOException {
		ModConfig.getCommonConfig().setString(ConfigKeys.SELECTED_MODEL, temp(skin.model(), opened.readAllBytes()));
		ModConfig.getCommonConfig().save();
	}

	@Override
	public void vh3() {
		MinecraftClientAccess.get().sendSkinUpdate();
	}
}
