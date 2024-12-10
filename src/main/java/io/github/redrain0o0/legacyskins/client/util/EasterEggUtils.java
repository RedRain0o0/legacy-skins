package io.github.redrain0o0.legacyskins.client.util;

import io.github.redrain0o0.legacyskins.client.screen.EScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;

import java.util.Calendar;

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

	private static final int luckyNumber = (int) (Math.random() * 100);
	public static boolean eEasterEgg() {
		if (EScreen.ticky > 15) return false;
		if (!isAprilFools()) return false;
		if (luckyNumber > 30) return false;
		return true;
	}

	private static boolean isAprilFools() {
		Calendar calendar = Calendar.getInstance();
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		return month == Calendar.APRIL && day == 1;
	}
}
