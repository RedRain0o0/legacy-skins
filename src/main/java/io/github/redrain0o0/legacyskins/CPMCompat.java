package io.github.redrain0o0.legacyskins;

import com.mojang.authlib.GameProfile;
import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;
import com.tom.cpm.shared.config.ConfigKeys;
import com.tom.cpm.shared.config.ModConfig;
import io.github.redrain0o0.legacyskins.util.SkinTextureToCustomPlayerModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CPMCompat implements ICPMPlugin {

	public static IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer;
	private static Supplier<IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile>> supplier;
	private static BiFunction<String, InputStream, IClientAPI.LocalModel> loadModel;
	public static IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> createRenderer() {
		return supplier.get();
	}
	public static IClientAPI.LocalModel loadModel(String name, InputStream stream) {
		return loadModel.apply(name, stream);
	}

	@Override
	public void initClient(IClientAPI api) {
		// TODO add a config option for this?
		ModConfig.getCommonConfig().setBoolean(ConfigKeys.TITLE_SCREEN_BUTTON, Legacyskins.lazyInstance().showSkinEditorButton());
		Legacyskins.LOGGER.info("CPMCompat client initialized.");
		loadModel = (name, b) -> {
			// why does java require this
			try {
				return api.loadModel(name, b);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		};
		supplier = () -> api.createPlayerRenderer(Model.class, ResourceLocation.class, RenderType.class, MultiBufferSource.class, GameProfile.class);
	}

	@Override
	public void initCommon(ICommonAPI api) {
	}

	@Override
	public String getOwnerModId() {
		return "legacyskins";
	}
}
