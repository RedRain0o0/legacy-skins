package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpm.shared.editor.Editor;
import com.tom.cpm.shared.editor.gui.EditorGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditorGui.class)
public interface EditorGuiAccessor {
	@Accessor
	Editor getEditor();

	@Accessor
	void setEditor(Editor editor);
}
