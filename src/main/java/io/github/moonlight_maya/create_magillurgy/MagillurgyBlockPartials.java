package io.github.moonlight_maya.create_magillurgy;

import com.jozufozu.flywheel.core.PartialModel;

public class MagillurgyBlockPartials {

	public static final PartialModel

	VAPORIZER_COIL_1 = block("vaporizer/coil1"),
	VAPORIZER_COIL_2 = block("vaporizer/coil2"),
	VAPORIZER_COIL_3 = block("vaporizer/coil3"),
	VAPORIZER_COIL_4 = block("vaporizer/coil4"),
	VAPORIZER_LENS = block("vaporizer/lens");

	private static PartialModel block(String path) {
		return new PartialModel(MagillurgyAddon.id("block/" + path));
	}

	//Load this class to init static fields
	public static void register() {}
}
