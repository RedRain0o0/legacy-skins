//? if figurac {
package io.github.redrain0o0.legacyskins.client.screen;

import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.util.FiguraUtils;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.AvatarManager;
import org.figuramc.figura.avatar.local.LocalAvatarLoader;
import org.figuramc.figura.backend2.NetworkStuff;
import org.figuramc.figura.gui.widgets.lists.AvatarList;

import java.io.IOException;
import java.nio.file.Path;

public class GeneralFiguraQ implements IGeneralFiguraQ {
	boolean f;
	@Override
	public void q(LegacySkin skin) {
		f = true;
		Avatar avatar = null;
		Path path = null;
		try {
			avatar = FiguraUtils.loadAvatar(path = FiguraUtils.loadAvatar(skin));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		avatar.nbt.getCompound("metadata")/*? if >=1.21.5 {*/.orElseThrow()/*?}*/.putString("uuid", FiguraMod.getLocalPlayerUUID().toString());
		AvatarList.selectedEntry = path;
		AvatarManager.loadLocalAvatar(path);
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception e) {}
		NetworkStuff.uploadAvatar(avatar);
	}

	@Override
	public void q2() {
		if (f) NetworkStuff.deleteAvatar(null);
		AvatarManager.clearAvatars(FiguraMod.getLocalPlayerUUID());
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception ignored) {}
		AvatarManager.localUploaded = true;
		AvatarList.selectedEntry = null;
		NetworkStuff.auth();
		f = false;
	}

	@Override
	public void q3() {
		FiguraUtils.onWorldTick();
	}

	@Override
	public void q4(Runnable runnable) {
		FiguraUtils.deferTillClientWorldLoad(runnable);
	}

	@Override
	public void q5() {
		FiguraUtils.clearWorldTickActions();
	}

	@Override
	public void q6() {
		AvatarManager.clearAvatars(FiguraMod.getLocalPlayerUUID());
		try {
			LocalAvatarLoader.loadAvatar(null, null);
		} catch (Exception ignored) {}
		AvatarManager.localUploaded = true;
		AvatarList.selectedEntry = null;
	}
}
//?}