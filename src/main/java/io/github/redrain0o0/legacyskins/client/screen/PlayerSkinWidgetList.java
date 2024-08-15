package io.github.redrain0o0.legacyskins.client.screen;

public class PlayerSkinWidgetList {
	PlayerSkinWidget element1;
	PlayerSkinWidget element2;
	PlayerSkinWidget element3;
	PlayerSkinWidget element4;
	PlayerSkinWidget element5;
	// At most 5 elements rendered at one time.
	//PlayerSkinWidget element6;
	public PlayerSkinWidget[] widgets = new PlayerSkinWidget[5];
	public void sortForIndex(int index) {
		// 0 -> [4, 5, 1, 2, 3]
		// 1 -> [5, 6, 2, 3, 4]
		// etc
		element1 = get(index - 2);
		element2 = get(index - 1);
		element3 = get(index);
		element4 = get(index + 1);
		element5 = get(index + 2);
		//element6 = get(index + 3);
	}

	private PlayerSkinWidget get(int index) {
		while (index < 0) {
			index = widgets.length - index;
		}
		return widgets[index];
	}
}
