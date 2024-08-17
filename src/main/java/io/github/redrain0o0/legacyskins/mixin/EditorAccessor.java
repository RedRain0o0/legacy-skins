package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpm.shared.editor.Editor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Editor.class)
public interface EditorAccessor {
	@Invoker
	void callSetPixel(int x, int y, int color);
}
