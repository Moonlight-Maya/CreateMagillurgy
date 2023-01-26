package io.github.moonlight_maya.create_magillurgy.mixin;

import io.github.moonlight_maya.create_magillurgy.client.MagillurgyAddonClient;
import net.minecraft.world.phys.Vec3;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BlastFurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@Mixin(BlastFurnaceBlock.class)
public class BlastFurnaceBlockMixin {

	@Inject(method = "animateTick", at = @At("HEAD"))
	public void spawnMagicParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (state.getValue(BlockStateProperties.LIT)) {
			Vec3 particlePos = Vec3.upFromBottomCenterOf(pos, 1.0);
			Vec3 particleVel = new Vec3(Math.random() * 0.1 - 0.05, Math.random()*0.01, Math.random() * 0.1 - 0.05);
			MagillurgyAddonClient.CLIENT_PARTICLES.addParticle(0, particlePos, particleVel);
		}
	}
}
