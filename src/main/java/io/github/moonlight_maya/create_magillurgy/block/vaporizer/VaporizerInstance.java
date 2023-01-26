package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.jozufozu.flywheel.api.Instancer;
import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.jozufozu.flywheel.core.Materials;
import com.jozufozu.flywheel.core.materials.oriented.OrientedData;
import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.content.contraptions.base.flwdata.RotatingData;
import com.simibubi.create.content.contraptions.relays.encased.EncasedCogInstance;
import com.simibubi.create.foundation.render.AllMaterialSpecs;

import io.github.moonlight_maya.create_magillurgy.client.MagillurgyBlockPartials;
import io.github.moonlight_maya.create_magillurgy.client.MagillurgyRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

//For instanced (fast) rendering
public class VaporizerInstance extends EncasedCogInstance implements DynamicInstance {

	private final OrientedData innerBarrel;
	private final OrientedData outerBarrel;
	private final OrientedData lens;

	private final VaporizerTileEntity vaporizer;

	public VaporizerInstance(MaterialManager modelManager, VaporizerTileEntity tile) {
		super(modelManager, tile, false);
		innerBarrel = materialManager.cutout(MagillurgyRenderTypes.cutoutNoCull()).material(Materials.ORIENTED).getModel(MagillurgyBlockPartials.VAPORIZER_BARREL_INNER, blockState).createInstance();
		outerBarrel = materialManager.cutout(MagillurgyRenderTypes.cutoutNoCull()).material(Materials.ORIENTED).getModel(MagillurgyBlockPartials.VAPORIZER_BARREL_OUTER, blockState).createInstance();
		lens = materialManager.transparent(RenderType.translucentNoCrumbling()).material(Materials.ORIENTED).getModel(MagillurgyBlockPartials.VAPORIZER_LENS, blockState).createInstance();
		vaporizer = tile;
		beginFrame();
	}

	@Override
	public void beginFrame() {
		float extension = vaporizer.getExtension(AnimationTickHolder.getPartialTicks());
		BlockPos instancePos = getInstancePosition();
		outerBarrel.setPosition(instancePos);
		innerBarrel.setPosition(instancePos);
		lens.setPosition(instancePos);

		float heightOffset = Mth.lerp(extension, 0, 2.75f/16);
		innerBarrel.nudge(0, -heightOffset, 0);
		lens.nudge(0, -heightOffset, 0);
		lens.setColor((byte) 0, (byte) 255, (byte) 255, (byte) 128);
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
		relight(pos.below(), innerBarrel);
		relight(pos.below(), outerBarrel);
		relight(pos.below(), lens);
	}

	@Override
	public void remove() {
		super.remove();
		innerBarrel.delete();
		outerBarrel.delete();
		lens.delete();
	}
}
