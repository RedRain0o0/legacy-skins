package io.github.redrain0o0.legacyskins.util;
//? if >=1.20.2 {
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
import io.github.redrain0o0.legacyskins.mixin.DefaultPlayerSkinAccessor;
import io.github.redrain0o0.legacyskins.mixin.EditorAccessor;
import io.github.redrain0o0.legacyskins.mixin.EditorGuiAccessor;
import io.github.redrain0o0.legacyskins.mixin.GeneratorsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
	public static void main(String[] args) throws Throwable {
		convert0(Files.readAllBytes(Path.of("steve.png")), false, Path.of("exported.cpmmodel"));
	}
	public static void convert(ResourceLocation texture, boolean slim, Path exportLoc) {
		try {
			convert0(Minecraft.getInstance().getResourceManager().getResourceOrThrow(texture).open().readAllBytes(), slim, exportLoc);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	public static void exportDefaultSkins() {
		Path path = Path.of("default-skin-export");
		path.toFile().mkdirs();
		for (PlayerSkin defaultSkin : DefaultPlayerSkinAccessor.getDefaultSkins()) {
			PlayerSkin.Model model = defaultSkin.model();
			ResourceLocation texture = defaultSkin.texture();
			String string = texture.toString();
			String[] split = string.split("/");
			String fileName = split[split.length - 2] + "/" + split[split.length - 1].split("\\.")[0] + ".cpmmodel";
			path.resolve(fileName).getParent().toFile().mkdirs();
			convert(texture, model.id().equals("slim"), path.resolve(fileName));
		}
		System.out.println("Exported default skins");
	}

	// Simulate the CPM GUI to create and export skins
	public static void convert0(byte[] texture, boolean slim, Path exportLoc) throws Throwable {
		UI gui = (UI) Proxy.newProxyInstance(SkinTextureToCustomPlayerModel.class.getClassLoader(), new Class[]{UI.class}, new AbstractInvocationHandler() {
			@Override
			protected Object handleInvocation(Object proxy, Method method,  Object[] args) throws Throwable {
				if (method.getName().equals("displayMessagePopup")) {
					System.out.println(args[0] + ": " + args[1]);
					return null;
					//if (args[0] instanceof String str && str.toLowerCase(Locale.ROOT).contains("error")) throw new RuntimeException(str + ", " + (String) args[1]);
				}
				if (method.getName().equals("displayPopup")) {
					throw new Throwable(method.getName());
				}
				if (method.getName().equals("executeLater")) {
					((Runnable) args[0]).run();
					return null;
				}
				if (method.getName().equals("i18nFormat")) {
					return I18n.get((String) args[0], (Object[]) args[1]);
				}
				System.out.println("Invoked " + method.getName() + " " + Arrays.toString(args));
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
		Image image = Image.loadFrom(new ByteArrayInputStream(texture));
		//byte[] bytes = texture;
//		Files.write(Path.of("by.png"), bytes);
//		File f = Path.of("by.png").toFile();
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				((EditorAccessor)editor).callSetPixel(x, y, image.getRGB(x, y));
			}
		}
		editor.getTextureProvider().getImage().storeTo(new File("gwlp.png"));
		editor.restitchTextures();

		editor.updateGui();
		editor.customSkinType = true;
		GeneratorsAccessor.callAddSkinLayer(editor);

		Generators.convertModel(editor);
		editor.save(new File("help.cpmproject")).join();
		System.out.println("Saved to " + new File("help.cpmproject").getAbsolutePath().toString());
		editor.load(new File("help.cpmproject"));
		ModelDescription modelDescription = new ModelDescription();
		editor.description = modelDescription;
		modelDescription.name = "";
		modelDescription.desc = "";

		editor.tick();
		editor.tick();
		editor.tick();
		editor.restitchTextures();
		for (ModelElement element : editor.elements) {
			element.preRender();
			element.postRender();
		}

		editor.reloadSkin();
		System.out.println(editor.skinType);
		Exporter.exportModel(editor, gui, exportLoc.toFile(), modelDescription, false);
	}
}
//?}