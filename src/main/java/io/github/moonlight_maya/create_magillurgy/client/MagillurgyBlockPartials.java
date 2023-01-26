package io.github.moonlight_maya.create_magillurgy.client;

import com.jozufozu.flywheel.core.PartialModel;

import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;

public class MagillurgyBlockPartials {

	public static final PartialModel

	VAPORIZER_BARREL_INNER = block("vaporizer/barrel_inner"),
	VAPORIZER_BARREL_OUTER = block("vaporizer/barrel_outer"),
	VAPORIZER_LENS = block("vaporizer/lens");

	private static PartialModel block(String path) {
		return new PartialModel(MagillurgyAddon.id("block/" + path));
	}

	//Load this class to init static fields
	public static void init() {}
}
