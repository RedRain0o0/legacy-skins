//? if figurac {
package io.github.redrain0o0.legacyskins.client.util;

import io.github.redrain0o0.legacyskins.LegacySkins;
import io.github.redrain0o0.legacyskins.client.LegacySkin;
import io.github.redrain0o0.legacyskins.client.LegacySkinPack;
import io.github.redrain0o0.legacyskins.client.LegacySkinsClient;
import io.github.redrain0o0.legacyskins.util.PlatformUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.figuramc.figura.avatar.Avatar;
import org.figuramc.figura.avatar.local.LocalAvatarLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FiguraUtils {
	static {
		//noinspection ResultOfMethodCallIgnored
		LocalAvatarLoader.getLastLoadedPath();
	}
	public static ILoadAvatar iLoadAvatar;
	private static final Map<Path, Avatar> A = new ConcurrentHashMap<>();
	private static final Map<LegacySkin, Path> B = new ConcurrentHashMap<>();
	private static final Map<LegacySkin, CompletableFuture<Void>> C = new ConcurrentHashMap<>();
	public static Avatar loadAvatar(Path path) {
		return A.computeIfAbsent(path, iLoadAvatar::call);
	}
	public static Avatar loadAvatarNow(LegacySkin legacySkin) {
		Path path = B.get(legacySkin);
		if (path == null) {
			C.computeIfAbsent(legacySkin, f -> CompletableFuture.runAsync(() -> {
				try {
					loadAvatar(legacySkin);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				C.remove(legacySkin);
			}));
			return null;
		}
		return loadAvatar(path);
	}
	public static Path loadAvatar(LegacySkin legacySkin) throws IOException {
		if (legacySkin.type() != LegacySkin.Type.FIGURA) throw new IllegalArgumentException("legacySkin was not a type FIGURA!");
		Path resolve = PlatformUtils.getGameDir().resolve(".ls-tempcache");
		resolve.toFile().mkdirs();
		Path resolve1 = resolve.resolve(legacySkin.model().hashCode() + "");
		if (!resolve1.toFile().isDirectory()) B.remove(legacySkin);
		return B.computeIfAbsent(legacySkin, c -> {
			ResourceLocation model = legacySkin.model();
			try {
				Resource resource = Minecraft.getInstance().getResourceManager().getResource(model).orElseThrow();
				try (ZipInputStream open = new ZipInputStream(resource.open())) {
					unzip(open, resolve1);
					loadAvatar(resolve1);
					return resolve1;
				} catch (Throwable t) {
					return st(t);
				}
			} catch (Throwable t) {
				throw new RuntimeException("error loading model of skin");
			}
		});
	}

	private static final List<Runnable> f = new CopyOnWriteArrayList<>();

	public static void deferTillClientWorldLoad(Runnable runnable) {
		if (Minecraft.getInstance().level == null) f.add(runnable);
		else runnable.run();
	}

	public static void onWorldTick() {
		if (LegacySkins.INSTANCE.getActiveSkinsConfig().getCurrentSkin().map(skin -> LegacySkinPack.list.get(skin.pack()).skins().get(skin.ordinal()).type()).orElse(null) != LegacySkin.Type.FIGURA) LegacySkinsClient.generalFiguraQ.q6();
		for (Runnable runnable : f) {
			f.remove(runnable);
			runnable.run();
		}
	}

	private static void unzip(ZipInputStream zis, Path destDir) throws IOException {
		byte[] buffer = new byte[1024];
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null) {
			while (zipEntry != null) {
				File newFile = newFile(destDir.toFile(), zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					// fix for Windows-created archives
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					// write file content
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}
		}

		zis.closeEntry();
		zis.close();
	}

	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	private static <T, E extends Throwable> T st(Throwable t) throws E {
		try {
			throw (E) t;
		} catch (Throwable r) {
			throw r;
		}
	}

	public static void clearWorldTickActions() {
		f.clear();
	}

	@FunctionalInterface
	public interface ILoadAvatar {
		@Nullable Avatar call(Path path);
	}
}
//?}