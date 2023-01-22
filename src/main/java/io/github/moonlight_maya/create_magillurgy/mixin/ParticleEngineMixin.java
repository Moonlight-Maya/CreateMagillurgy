package io.github.moonlight_maya.create_magillurgy.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;

import net.minecraft.client.renderer.LightTexture;

import net.minecraft.client.renderer.MultiBufferSource;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {
	@Inject(method = "render", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/particle/ParticleRenderType;end(Lcom/mojang/blaze3d/vertex/Tesselator;)V",
			shift = At.Shift.BEFORE
	))
	public void injected(PoseStack matrixStack, MultiBufferSource.BufferSource buffers, LightTexture lightTexture, Camera activeRenderInfo, float partialTicks, CallbackInfo ci) {
		MagillurgyAddon.TEST_PARTICLES.render(activeRenderInfo, partialTicks);
	}
}
