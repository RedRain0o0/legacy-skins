package io.github.redrain0o0.legacyskins.util;

import wily.legacy.client.screen.LegacyLoadingScreen;
import net.minecraft.network.chat.Component;
//? if legacy4j: >=1.7.5 {
import wily.legacy.client.LegacyOption;
//?} else
/*import wily.legacy.util.ScreenUtil;*/

public class Legacy4JUtils {
	private static Legacy4JOptions INSTANCE;
	public static Legacy4JOptions options() {
		if (INSTANCE == null) INSTANCE = new Legacy4JOptions() {
			@Override
			public Double interfaceSensitivity() {
				//? if legacy4j: <1.7.5 {
				/*return ScreenUtil.getLegacyOptions().interfaceSensitivity().get();
				*///?} else
				return LegacyOption.interfaceSensitivity.get();
			}
		};
		return INSTANCE;
	}

	public static LegacyLoadingScreenInterface loadingScreen(LegacyLoadingScreen loadingScreen) {
		return new LegacyLoadingScreenInterface() {
			@Override
			public void setGenericLoading(boolean genericLoading) {
				//? if legacy4j: <1.7.5 {
				/*loadingScreen.genericLoading = true;
				*///?} else
				loadingScreen.setGenericLoading(genericLoading);
			}

			@Override
			public void setLoadingHeader(Component loadingHeader) {
				//? if legacy4j: <1.7.5 {
				/*loadingScreen.lastLoadingHeader = loadingHeader;
				*///?} else
				loadingScreen.setLoadingHeader(loadingHeader);
			}

			@Override
			public void setLoadingStage(Component loadingStage) {
				//? if legacy4j: <1.7.5 {
				/*loadingScreen.lastLoadingStage = loadingStage;
				*///?} else
				loadingScreen.setLoadingStage(loadingStage);
			}

			@Override
			public void setProgress(int progress) {
				//? if legacy4j: <1.7.5 {
				/*loadingScreen.progress = progress;
				*///?} else
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
