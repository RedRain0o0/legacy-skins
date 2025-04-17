package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpl.config.ConfigEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ConfigEntry.class)
public class ConfigEntryMixin {
	@Shadow protected Map<String, ConfigEntry> entries;

	@Shadow protected Map<String, Object> data;

	//?if !forge && !(neoforge && =1.20.4) {
	@com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod(method = "getEntry", remap = false)
	public ConfigEntry legacyskins$getEntry(String name, com.llamalad7.mixinextras.injector.wrapoperation.Operation<ConfigEntry> original) {
		synchronized (entries) {
			synchronized (data) {
				return original.call(name);
			}
		}
	}
	//?}
}
