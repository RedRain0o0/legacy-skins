package io.github.redrain0o0.legacyskins.util;

import com.google.common.reflect.AbstractInvocationHandler;
import com.tom.cpl.gui.*;
import com.tom.cpl.item.Stack;
import com.tom.cpl.text.IText;
import com.tom.cpl.util.Image;
import com.tom.cpm.shared.editor.ETextures;
import com.tom.cpm.shared.editor.Editor;
import com.tom.cpm.shared.editor.Exporter;
import com.tom.cpm.shared.editor.Generators;
import com.tom.cpm.shared.editor.actions.ActionBuilder;
import com.tom.cpm.shared.editor.elements.ModelElement;
import com.tom.cpm.shared.editor.gui.EditorGui;
import com.tom.cpm.shared.editor.util.ModelDescription;
import com.tom.cpm.shared.model.PlayerModelParts;
import com.tom.cpm.shared.model.SkinType;
import com.tom.cpm.shared.model.TextureSheetType;
import com.tom.cpm.shared.util.PlayerModelLayer;
import io.github.redrain0o0.legacyskins.mixin.EditorAccessor;
import io.github.redrain0o0.legacyskins.mixin.EditorGuiAccessor;
import io.github.redrain0o0.legacyskins.mixin.GeneratorsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

public class SkinTextureToCustomPlayerModel {
	public static void main(String[] args) {
		convert(null, false);
	}
	public static void convert(ResourceLocation texture, boolean slim) {
		try {
			convert0(texture, slim);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	public static void convert0(ResourceLocation texture, boolean slim) throws Throwable {
		UI gui = (UI) Proxy.newProxyInstance(SkinTextureToCustomPlayerModel.class.getClassLoader(), new Class[]{UI.class}, new AbstractInvocationHandler() {
			@Override
			protected Object handleInvocation(Object proxy, Method method,  Object[] args) throws Throwable {
				System.out.println("Invoked " + method.getName() + " " + Arrays.toString(args));
				if (method.getName().equals("displayMessagePopup")) {
					if (args[0] instanceof String str && str.toLowerCase(Locale.ROOT).contains("error")) throw new RuntimeException(str + ", " + (String) args[1]);
				}
				if (method.getName().equals("displayPopup")) {
					throw new Throwable(method.getName());
				}
				if (method.getName().equals("executeLater")) {
					((Runnable) args[0]).run();
				}
				return null;
			}
		});
		Editor editor = new Editor();
		editor.ui = gui;
		editor.reinit();
		//editor.mode
		editor.loadDefaultPlayerModel();
		editor.customSkinType = true;
		editor.skinType = slim ? SkinType.SLIM : SkinType.DEFAULT;
		editor.vanillaSkin = editor.skinType.getSkinTexture();
		editor.updateGui();
		Image image = Image.loadFrom(Minecraft.getInstance().getResourceManager().getResourceOrThrow(texture).open());
		byte[] bytes = Minecraft.getInstance().getResourceManager().getResourceOrThrow(texture).open().readAllBytes();
		Files.write(Path.of("by.png"), bytes);
		File f = Path.of("by.png").toFile();
		//editor.setTexSize(image.getWidth(), image.getHeight());
		//editor.drawPixel();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				//editor.penColor = image.getRGB(x, y);

				// image.getRGB(x, y)
				((EditorAccessor)editor).callSetPixel(x, y, new Color((float) Math.random(), (float) Math.random(), (float) Math.random()).getRGB());
				//((EditorAccessor) editor).callSetPixel(x, y, 0x00000000);
				//editor.drawPixel(x, y, true);
			}
		}
		editor.getTextureProvider().getImage().storeTo(new File("gwlp.png"));
		editor.restitchTextures();
//		ETextures tex = editor.getTextureProvider();
//		if(tex != null) {
//			ActionBuilder ab = editor.action("loadTexture");
//			ab.updateValueOp(tex, tex.file, f, (a, b) -> a.file = b);
//			editor.reloadSkin(ab, tex, f);
//			editor.updateGui();
//		}
		//editor.drawPixel();
		//editor.getTextureProvider().refreshTexture();

		editor.updateGui();

		editor.save(new File("help.cpmproject")).join();
		editor.load(new File("help.cpmproject"));
//		editor.loadDefaultPlayerModel();
//		editor.updateGui();
		ModelDescription modelDescription = new ModelDescription();
		editor.description = modelDescription;
		modelDescription.name = "";
		modelDescription.desc = "";

		editor.tick();
		editor.tick();
		editor.tick();
		editor.restitchTextures();
		// THIS THING JUST CREATES BLACK LATERS
		for (ModelElement element : editor.elements) {
			element.preRender();
			element.postRender();
		}

		//GeneratorsAccessor.callAddSkinLayer(editor);
		//editor.
		//editor.

		editor.reloadSkin();
		//Exporter.exportTempModel()
		Exporter.exportModel(editor, gui, Path.of("exported.cpmmodel").toFile(), modelDescription, false);
	}
}
