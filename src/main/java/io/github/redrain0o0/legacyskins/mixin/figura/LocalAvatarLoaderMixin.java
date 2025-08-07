//? if figurac {
package io.github.redrain0o0.legacyskins.mixin.figura;

import io.github.redrain0o0.legacyskins.annotations.SurrogateMethod;
import io.github.redrain0o0.legacyskins.client.util.FiguraUtils;
import net.minecraft.nbt.CompoundTag;
import org.figuramc.figura.FiguraMod;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.UserData;
import org.figuramc.figura.avatar.local.LocalAvatarLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.nio.file.Path;
@Mixin(LocalAvatarLoader.class)
public abstract class LocalAvatarLoaderMixin {
	static {
		FiguraUtils.iLoadAvatar = LocalAvatarLoaderMixin::loadAvatarLS;
	}


	/*
	 * Surrogate for synthetic lambda method in LocalAvatarLoader#loadAvatar(Path,UserData)
	 * The reason for calling the surrogate method instead of calling the synthetic method directly is that the synthetic method's name is unstable and could vary depending on the Figura version or the Minecraft version.
	 */
	@SuppressWarnings("MissingUnique")
	@SurrogateMethod
	private static native void ls$loadAvatar(Path path, UserData data);

	@Nullable
	@Unique
	private static Avatar loadAvatarLS(Path path) {
		Avatar[] avatarP = new Avatar[1];
		try {
			ls$loadAvatar(path, new UserData(FiguraMod.getLocalPlayerUUID()) {
				@Override
				public void loadAvatar(CompoundTag nbt) {
					Avatar avatar = new Avatar(FiguraMod.getLocalPlayerUUID());
					avatar.load(nbt);
					avatarP[0] = avatar;
				}
			});
		} catch (Throwable t) {
			return null;
		}
		return avatarP[0];
	}
}
//?} else {
/*package io.github.redrain0o0.legacyskins.mixin.figura;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class LocalAvatarLoaderMixin {

}
*///?}