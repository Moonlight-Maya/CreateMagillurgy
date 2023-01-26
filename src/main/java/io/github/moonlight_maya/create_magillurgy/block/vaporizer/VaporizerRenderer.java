package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.jozufozu.flywheel.backend.Backend;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;

import com.simibubi.create.foundation.fluid.FluidRenderer;

import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.util.Mth;

//For immediate (slower but sometimes necessary for compat) rendering
public class VaporizerRenderer extends KineticTileEntityRenderer {
	public VaporizerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	protected void renderSafe(KineticTileEntity te, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
		super.renderSafe(te, partialTicks, ms, buffer, light, overlay);
		if (!(te instanceof VaporizerTileEntity vaporizer)) return;
		//Render fluids here since we can't do it instanced
		FluidStack upperStack = vaporizer.fluidInUpperTank();
		FluidStack lowerStack = vaporizer.fluidInLowerTank();
		float upperTankFraction = (float) upperStack.getAmount() / VaporizerTileEntity.UPPER_TANK_CAPACITY;
		float lowerTankFraction = (float) lowerStack.getAmount() / VaporizerTileEntity.COST_PER_OPERATION;
		if (upperTankFraction > 0) {
			float minHoriz = 2f/16 + 0.001f;
			float minVert = 11f/16;
			float maxHoriz = 1 - minHoriz;
			float maxVert = 15f/16;
			float fluidTop = Mth.lerp(upperTankFraction, minVert, maxVert);
			FluidRenderer.renderFluidBox(upperStack, minHoriz, minVert, minHoriz, maxHoriz, fluidTop, maxHoriz, buffer, ms, light, false);
		}
		if (lowerTankFraction > 0) {
			float minHoriz = 5f/16 + 0.001f;
			float minVert = -3f/16;
			float maxHoriz = 1 - minHoriz;
			float maxVert = 2f/16;
			float fluidTop = Mth.lerp(lowerTankFraction, minVert, maxVert);
			FluidRenderer.renderFluidBox(lowerStack, minHoriz, minVert, minHoriz, maxHoriz, fluidTop, maxHoriz, buffer, ms, light, false);
		}
		//Render laser beam

		if (Backend.canUseInstancing(te.getLevel())) return;

		//Render main block
	}
}
