package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.content.contraptions.relays.encased.EncasedCogInstance;
import com.simibubi.create.foundation.render.AllMaterialSpecs;

import io.github.moonlight_maya.create_magillurgy.MagillurgyBlockPartials;
import net.minecraft.util.Mth;

//For instanced (fast) rendering
public class VaporizerInstance extends EncasedCogInstance implements DynamicInstance {

	private final OrientedData[] coilSegments;
	private final VaporizerTileEntity vaporizer;

	public VaporizerInstance(MaterialManager modelManager, VaporizerTileEntity tile) {
		super(modelManager, tile, false);
		coilSegments = new OrientedData[5];
		coilSegments[0] = getOrientedMaterial().getModel(MagillurgyBlockPartials.VAPORIZER_COIL_1, blockState).createInstance();
		coilSegments[1] = getOrientedMaterial().getModel(MagillurgyBlockPartials.VAPORIZER_COIL_2, blockState).createInstance();
		coilSegments[2] = getOrientedMaterial().getModel(MagillurgyBlockPartials.VAPORIZER_COIL_3, blockState).createInstance();
		coilSegments[3] = getOrientedMaterial().getModel(MagillurgyBlockPartials.VAPORIZER_COIL_4, blockState).createInstance();
		coilSegments[4] = getOrientedMaterial().getModel(MagillurgyBlockPartials.VAPORIZER_LENS, blockState).createInstance();
		vaporizer = tile;
		beginFrame();
	}

	@Override
	public void beginFrame() {
		//extension is 0 to 1, added some simple testing code here
		float extension = 1.0f;
		extension = (System.currentTimeMillis() % 5000) / 5000f; //0 to 1 every 5 seconds
		float proportion = 0.95f;
		if (extension < proportion) {
			extension /= proportion;
		} else {
			extension = Mth.lerp((extension - proportion) / (1 - proportion), 1f, 0f);
		}

		//Will later be based on the tile entity
		//float extension = vaporizer.getReadiness(AnimationTickHolder.getPartialTicks());
		for (OrientedData coilSegment : coilSegments)
			coilSegment.setPosition(getInstancePosition());

		float extension_pixels = extension * 0.5f; //in pixels, not 0 to 1
		coilSegments[0].nudge(0, 0, 0);
		coilSegments[1].nudge(0, -Math.min(extension_pixels, 0.0625f), 0);
		coilSegments[2].nudge(0, -Math.min(extension_pixels, 0.125f), 0);
		coilSegments[3].nudge(0, -Math.min(extension_pixels, 0.1875f), 0);
		coilSegments[4].nudge(0, -Math.min(extension_pixels, 0.1875f), 0);
	}

	@Override
	protected Instancer<RotatingData> getCogModel() {
		return materialManager.defaultSolid()
				.material(AllMaterialSpecs.ROTATING)
				.getModel(AllBlockPartials.SHAFTLESS_COGWHEEL, blockEntity.getBlockState());
	}

	@Override
	public void updateLight() {
		super.updateLight();
		for (OrientedData coilSegment : coilSegments) {
			relight(pos.below(), coilSegment);
		}
	}

	@Override
	public void remove() {
		super.remove();
		for (OrientedData segment : coilSegments)
			segment.delete();
	}
}
