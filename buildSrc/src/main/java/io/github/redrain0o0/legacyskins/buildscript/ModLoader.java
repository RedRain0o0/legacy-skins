package io.github.redrain0o0.legacyskins.buildscript;

public enum ModLoader {
	FABRIC("fabric", "Fabric"),
	FORGE("forge", "Forge"),
	NEOFORGE("neoforge", "NeoForge");

	public final String friendlyName;
	public final String formalName;

	ModLoader(String friendlyName, String formalName) {
		this.friendlyName = friendlyName;
		this.formalName = formalName;
	}

	public boolean isFabricLike() {
		return this == FABRIC;
	}

	public boolean isForgeLike() {
		return this == FORGE || this == NEOFORGE;
	}
}
