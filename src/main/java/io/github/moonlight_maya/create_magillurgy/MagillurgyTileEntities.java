package io.github.moonlight_maya.create_magillurgy;

import com.tterrag.registrate.util.entry.BlockEntityEntry;

import io.github.moonlight_maya.create_magillurgy.block.vaporizer.VaporizerInstance;
import io.github.moonlight_maya.create_magillurgy.block.vaporizer.VaporizerRenderer;
import io.github.moonlight_maya.create_magillurgy.block.vaporizer.VaporizerTileEntity;

public class MagillurgyTileEntities {

	public static final BlockEntityEntry<VaporizerTileEntity> VAPORIZER = MagillurgyAddon.REGISTRATE
			.tileEntity("vaporizer", VaporizerTileEntity::new)
			.instance(() -> VaporizerInstance::new)
			.validBlocks(MagillurgyBlocks.VAPORIZER)
			.renderer(() -> VaporizerRenderer::new)
			.register();


	//Just loading this class by calling this method is enough to register tile entities
	public static void register() {}
}
