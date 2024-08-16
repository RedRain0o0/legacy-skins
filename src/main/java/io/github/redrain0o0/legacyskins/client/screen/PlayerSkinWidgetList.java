package io.github.redrain0o0.legacyskins.client.screen;

import java.util.ArrayList;
import java.util.List;

public class PlayerSkinWidgetList {
	PlayerSkinWidget elementN1;
	PlayerSkinWidget element0;
	PlayerSkinWidget element1;
	PlayerSkinWidget element2;
	PlayerSkinWidget element3;
	PlayerSkinWidget element4;
	PlayerSkinWidget element5;
	// At most 6 elements rendered at one time.
	PlayerSkinWidget element6;
	PlayerSkinWidget element7;
	public List<PlayerSkinWidget> widgets = new ArrayList<>();
	public static PlayerSkinWidgetList of(
			PlayerSkinWidget... playerSkinWidgets
	) {
		PlayerSkinWidgetList playerSkinWidgetList = new PlayerSkinWidgetList();
		playerSkinWidgetList.widgets.addAll(List.of(playerSkinWidgets));
		return playerSkinWidgetList;
	}
	@SuppressWarnings("SuspiciousNameCombination")
	public void sortForIndex(int index) {
		widgets.forEach(a -> a.visible = false);
		// 0 -> [4, 5, 1, 2, 3]
		// 1 -> [5, 6, 2, 3, 4]
		// etc
		elementN1 = get(index - 4);elementN1.invisible(); elementN1.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET * 4, CENTER_Y, 0.8f);
		element0 = get(index - 3);element0.visible(); element0.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET * 3, CENTER_Y, 0.8f);
		element1 = get(index - 2);element1.visible(); element1.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET * 2, CENTER_Y, 0.8f);
		element2 = get(index - 1);element2.visible(); element2.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET, CENTER_Y, 0.8f);
		element3 = get(index);element3.visible(); element3.beginInterpolation(0, 0, CENTER_X, CENTER_Y, 1);
		element4 = get(index + 1);element4.visible(); element4.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET, CENTER_Y, 0.8f);
		element5 = get(index + 2);element5.visible(); element5.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET * 2, CENTER_Y, 0.8f);
		element6 = get(index + 3);element6.visible(); element6.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET * 3, CENTER_Y, 0.8f);
		element7 = get(index + 4);element7.invisible(); element7.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET * 4, CENTER_Y, 0.8f);
	}

	private static final int CENTER_X = 250;
	private static final int CENTER_Y = 150;
	private static final int OFFSET = 75;
	private static final float FACING_FROM_LEFT = 30f;
	private static final float FACING_FROM_RIGHT = -30f;

	private PlayerSkinWidget get(int index) {
		while (index < 0) {
			index = widgets.size() - index;
		}
		while (index >= widgets.size()) {
			index = index - widgets.size();
		}
		return widgets.get(index);
	}
}
