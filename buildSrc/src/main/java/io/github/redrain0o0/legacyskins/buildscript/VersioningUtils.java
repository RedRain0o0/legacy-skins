package io.github.redrain0o0.legacyskins.buildscript;

import java.util.Map;

public class VersioningUtils {
	private static final Map<String, String> MAP = Map.of(
			"1.7.10.2504.3", "1.8.0-beta.2504.3",
			"1.8.0.2517.1", "1.8.1-beta.2517.1",
			"1.8.2.2529.2", "1.8.3-beta.2529.2",
			"1.8.2.2530.1", "1.8.3-beta.2530.1"
	);
	public static String semverifyL4JVer(String inputVersion) {
		String suffix = "";
		if (inputVersion.endsWith("+fabric")) {
			suffix = "+fabric";
		} else if (inputVersion.endsWith("+neoforge")) {
			suffix = "+neoforge";
		} else if (inputVersion.endsWith("+forge")) {
			suffix = "+forge";
		}
		inputVersion = inputVersion.substring(0, inputVersion.length() - suffix.length());
		if (inputVersion.length() - inputVersion.replace(".", "").length() > 2) {
			if (MAP.containsKey(inputVersion)) return MAP.get(inputVersion) + suffix;
			throw new IllegalStateException("Unable to correct version " + inputVersion);
		}
		return inputVersion + suffix;
	}
}
