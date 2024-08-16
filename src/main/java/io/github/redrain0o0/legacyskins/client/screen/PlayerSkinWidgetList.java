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
	int x;
	int y;
	int index = 0;
	public List<PlayerSkinWidget> widgets = new ArrayList<>();
	public static PlayerSkinWidgetList of(
			int x, int y,
			PlayerSkinWidget... playerSkinWidgets
	) {
		PlayerSkinWidgetList playerSkinWidgetList = new PlayerSkinWidgetList();
		playerSkinWidgetList.CENTER_X = x;
		playerSkinWidgetList.CENTER_Y = y;
		playerSkinWidgetList.widgets.addAll(List.of(playerSkinWidgets));
		int i = 0;
		//playerSkinWidgetList.sortForIndex(0);
		return playerSkinWidgetList;
	}
	@SuppressWarnings("SuspiciousNameCombination")
	public void sortForIndex(int index) {
		this.index = index;
		//widgets.forEach(a -> a.visible = false);
		// 0 -> [4, 5, 1, 2, 3]
		// 1 -> [5, 6, 2, 3, 4]
		// etc
		elementN1 = get(index - 4);elementN1.interactable = false;elementN1.invisible(); elementN1.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET * 4 + 80, CENTER_Y + VERTICAL_OFFSET, 0.4f);
		element0 = get(index - 3);element0.interactable = false;element0.visible(); element0.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET * 3 + 60, CENTER_Y + VERTICAL_OFFSET, 0.5f);
		element1 = get(index - 2);element1.interactable = false;element1.visible(); element1.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET * 2 + 40, CENTER_Y + VERTICAL_OFFSET, 0.6f);
		element2 = get(index - 1);element2.interactable = false;element2.visible(); element2.beginInterpolation(0, FACING_FROM_LEFT, CENTER_X - OFFSET + 20, CENTER_Y + VERTICAL_OFFSET, 0.75f);
		element3 = get(index);element3.interactable = true;element3.visible(); element3.beginInterpolation(0, 0, CENTER_X, CENTER_Y, 1);
		element4 = get(index + 1);element4.interactable = false;element4.visible(); element4.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET, CENTER_Y + VERTICAL_OFFSET, 0.75f);
		element5 = get(index + 2);element5.interactable = false;element5.visible(); element5.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET * 2, CENTER_Y + VERTICAL_OFFSET * 2, 0.6f);
		element6 = get(index + 3);element6.interactable = false;element6.visible(); element6.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET * 3, CENTER_Y + VERTICAL_OFFSET * 3, 0.5f);
		element7 = get(index + 4);element7.interactable = false;element7.invisible(); element7.beginInterpolation(0, FACING_FROM_RIGHT, CENTER_X + OFFSET * 4, CENTER_Y + VERTICAL_OFFSET * 4, 0.4f);
	}

	private int CENTER_X = 250;
	private int CENTER_Y = 150;
	private static final int VERTICAL_OFFSET = 7;
	private static final int OFFSET = 80;
	private static final float FACING_FROM_LEFT = 30f;
	private static final float FACING_FROM_RIGHT = -30f;

	private PlayerSkinWidget get(int index) {
		while (index < 0) {
			index += widgets.size();
		}
		while (index >= widgets.size()) {
			index = index - widgets.size();
		}
		return widgets.get(index);
	}
}
