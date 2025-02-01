package io.github.redrain0o0.legacyskins.util;

import wily.legacy.client.screen.LegacyLoadingScreen;
import net.minecraft.network.chat.Component;
import wily.legacy.client.LegacyOptions;

// pre-1.7.5 stuff has been removed as we don't support those anymore
public class Legacy4JUtils {
	private static Legacy4JOptions INSTANCE;
	public static Legacy4JOptions options() {
		if (INSTANCE == null) INSTANCE = new Legacy4JOptions() {
			@Override
			public Double interfaceSensitivity() {
				return LegacyOptions.interfaceSensitivity.get();
			}
		};
		return INSTANCE;
	}

	public static LegacyLoadingScreenInterface loadingScreen(LegacyLoadingScreen loadingScreen) {
		return new LegacyLoadingScreenInterface() {
			@Override
			public void setGenericLoading(boolean genericLoading) {
				loadingScreen.setGenericLoading(genericLoading);
			}

			@Override
			public void setLoadingHeader(Component loadingHeader) {
				loadingScreen.setLoadingHeader(loadingHeader);
			}

			@Override
			public void setLoadingStage(Component loadingStage) {
				loadingScreen.setLoadingStage(loadingStage);
			}

			@Override
			public void setProgress(int progress) {
				loadingScreen.setProgress(progress);
			}
		};
	}

	// Add stuff here when we need it
	public interface Legacy4JOptions {
		Double interfaceSensitivity();
	}

	public interface LegacyLoadingScreenInterface {
		void setGenericLoading(boolean genericLoading);
		void setLoadingHeader(Component loadingHeader);
		void setLoadingStage(Component loadingStage);
		void setProgress(int progress);
	}
}
