package io.github.redrain0o0.legacyskins.buildscript;

import org.gradle.api.Project;

import java.util.Locale;

public record ProjectInfo(String minecraftVersion, ModLoader loader) {
	public static ProjectInfo from(Project project) {
		String name = project.getName();
		String[] split = name.split("-");
		return new ProjectInfo(split[0], ModLoader.valueOf(split[1].toUpperCase(Locale.ROOT)));
	}
}
