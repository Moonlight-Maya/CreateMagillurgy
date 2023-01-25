package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.simibubi.create.content.contraptions.base.KineticTileEntityRenderer;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

//For immediate (slower but sometimes necessary for compat) rendering
public class VaporizerRenderer extends KineticTileEntityRenderer {



	public VaporizerRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}
}
