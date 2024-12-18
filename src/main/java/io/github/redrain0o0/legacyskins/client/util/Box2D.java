package io.github.redrain0o0.legacyskins.client.util;

public record Box2D(int x, int y, int width, int height) {
	public boolean isMouseInside(double mouseX, double mouseY) {
		return mouseX > x && mouseY > y && mouseX < x + width && mouseY < y + height;
	}
}
