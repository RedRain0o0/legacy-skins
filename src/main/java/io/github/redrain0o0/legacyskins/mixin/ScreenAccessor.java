package io.github.redrain0o0.legacyskins.mixin;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface ScreenAccessor {
	@SuppressWarnings("SpellCheckingInspection")
	@Accessor
	List<Renderable> getRenderables();
}
