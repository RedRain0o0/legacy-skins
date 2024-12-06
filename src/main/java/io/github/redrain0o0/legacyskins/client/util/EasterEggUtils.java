package io.github.redrain0o0.legacyskins.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

public enum EasterEggUtils {
	;
	@SuppressWarnings({"ConstantValue", "resource"})
	public static boolean isHardcoreMode() {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return false;
		Level level = player.level();
		if (level == null) return false;
		LevelData levelData = level.getLevelData();
		if (levelData == null) return false;
		return levelData.isHardcore();
	}
}
