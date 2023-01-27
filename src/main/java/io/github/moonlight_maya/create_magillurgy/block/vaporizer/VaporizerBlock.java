package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.simibubi.create.AllTileEntities;
import com.simibubi.create.content.contraptions.base.KineticBlock;
import com.simibubi.create.content.contraptions.relays.elementary.ICogWheel;
import com.simibubi.create.foundation.block.ITE;

import io.github.moonlight_maya.create_magillurgy.MagillurgyTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class VaporizerBlock extends KineticBlock implements ITE<VaporizerTileEntity>, ICogWheel {
	public VaporizerBlock(Properties properties) {
		super(properties);
	}

	public static final VoxelShape SHAPE = Shapes.join(
			Shapes.box(0, 0.125, 0, 1, 0.6875, 1),
			Shapes.box(0.0625, 0.6875, 0.0625, 0.9375, 1, 0.9375),
			BooleanOp.OR);

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		withTileEntityDo(level, pos, VaporizerTileEntity::updateSignal);
	}

	@Override
	public Direction.Axis getRotationAxis(BlockState state) {
		return Direction.Axis.Y;
	}

	@Override
	public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
		return false;
	}

	@Override
	public float getParticleTargetRadius() {
		return .85f;
	}

	@Override
	public float getParticleInitialRadius() {
		return .75f;
	}

	@Override
	public Class<VaporizerTileEntity> getTileEntityClass() {
		return VaporizerTileEntity.class;
	}

	@Override
	public BlockEntityType<? extends VaporizerTileEntity> getTileEntityType() {
		return MagillurgyTileEntities.VAPORIZER.get();
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter reader, BlockPos pos, PathComputationType type) {
		return false;
	}
}
