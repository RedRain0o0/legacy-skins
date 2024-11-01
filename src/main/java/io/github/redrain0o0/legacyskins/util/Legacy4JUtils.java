package io.github.redrain0o0.legacyskins.util;

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

	// Add stuff here when we need it
	public interface Legacy4JOptions {
		Double interfaceSensitivity();
	}
}
