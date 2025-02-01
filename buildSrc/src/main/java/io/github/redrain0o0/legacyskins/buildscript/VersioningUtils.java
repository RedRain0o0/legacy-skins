package io.github.redrain0o0.legacyskins.buildscript;

import java.util.Map;

public class VersioningUtils {
	private static final Map<String, String> MAP = Map.of(
			"1.7.10.2504.3", "1.8.0-beta.2504.3"
	);
	public static String semverifyL4JVer(String inputVersion) {
		if (inputVersion.length() - inputVersion.replace(".", "").length() > 2) {
			if (MAP.containsKey(inputVersion)) return MAP.get(inputVersion);
			throw new IllegalStateException("Unable to correct version " + inputVersion);
		}
		return inputVersion;
	}
}
