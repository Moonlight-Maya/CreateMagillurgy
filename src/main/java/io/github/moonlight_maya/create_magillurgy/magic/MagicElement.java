package io.github.moonlight_maya.create_magillurgy.magic;

public enum MagicElement {
	//Order is important, used elsewhere
	LIFE(0.0f, 0.8f, 0.0f),
	ENDER(0.4f, 0.0f, 1.0f),
	SOUL(0.0f, 1.0f, 1.0f),
	VOID(0.0f, 0.0f, 0.0f),
	ASTRAL(1.0f, 1.0f, 1.0f),
	NETHER(0.5f, 0f, 0f),
	ARCANE(1.0f, 0.4f, 1.0f);

	public final float r, g, b;
	MagicElement(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
