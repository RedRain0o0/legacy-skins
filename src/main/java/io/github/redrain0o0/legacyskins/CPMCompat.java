package io.github.redrain0o0.legacyskins;

import com.mojang.authlib.GameProfile;
import com.tom.cpm.api.ICPMPlugin;
import com.tom.cpm.api.IClientAPI;
import com.tom.cpm.api.ICommonAPI;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CPMCompat implements ICPMPlugin {

	public static IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> renderer;
	private static Supplier<IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile>> supplier;

	public static IClientAPI.PlayerRenderer<Model, ResourceLocation, RenderType, MultiBufferSource, GameProfile> createRenderer() {
		return supplier.get();
	}

	@Override
	public void initClient(IClientAPI api) {
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
