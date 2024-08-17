package io.github.redrain0o0.legacyskins.mixin;

import com.tom.cpm.shared.editor.Editor;
import com.tom.cpm.shared.editor.Generators;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Generators.class)
public interface GeneratorsAccessor {
	@Invoker
	static void callAddSkinLayer(Editor e) {
		throw new UnsupportedOperationException();
	}
}
