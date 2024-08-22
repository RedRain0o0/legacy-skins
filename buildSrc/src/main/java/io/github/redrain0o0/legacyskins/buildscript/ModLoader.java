package io.github.redrain0o0.legacyskins.buildscript;

public enum ModLoader {
	FABRIC("fabric"),
	FORGE("forge"),
	NEOFORGE("neoforge");

	public final String friendlyName;

	ModLoader(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public boolean isFabricLike() {
		return this == FABRIC;
	}

	public boolean isForgeLike() {
		return this == FORGE || this == NEOFORGE;
	}
}
