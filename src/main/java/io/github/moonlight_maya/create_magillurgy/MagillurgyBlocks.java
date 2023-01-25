package io.github.moonlight_maya.create_magillurgy;

import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.SharedProperties;
import com.simibubi.create.foundation.data.TagGen;
import com.tterrag.registrate.util.entry.BlockEntry;

import io.github.moonlight_maya.create_magillurgy.block.vaporizer.VaporizerBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MaterialColor;

public class MagillurgyBlocks {

	public static final BlockEntry<VaporizerBlock> VAPORIZER = MagillurgyAddon.REGISTRATE
			.block("vaporizer", VaporizerBlock::new)
			.initialProperties(SharedProperties::stone)
			.properties(p -> p.color(MaterialColor.STONE))
			.properties(BlockBehaviour.Properties::noOcclusion)
			.transform(TagGen.axeOrPickaxe())
			.blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
			.addLayer(() -> RenderType::cutoutMipped)
			.transform(BlockStressDefaults.setImpact(4.0))
//			.item(AssemblyOperatorBlockItem::new)
//			.transform(ModelGen.customItemModel())
			.register();


	//Just loading this class by calling this method is enough to register the blocks
	public static void register() {}
}
