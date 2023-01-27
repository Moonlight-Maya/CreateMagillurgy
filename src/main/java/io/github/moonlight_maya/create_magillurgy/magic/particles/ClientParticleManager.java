package io.github.moonlight_maya.create_magillurgy.magic.particles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import io.github.moonlight_maya.create_magillurgy.magic.Resonances;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

/**
 * Contains additional code related to rendering particles as well
 * as reading client-bound particle spawn packets.
 */

public class ClientParticleManager extends MagicParticleManager {


	private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(MagillurgyAddon.ID, "textures/magic.png");

	public ClientParticleManager(int initialSize) {
		super(initialSize);
	}

	public void handleParticlePacket(FriendlyByteBuf packet) {
		int[] resonances = packet.readVarIntArray();
		for (int resonance : resonances) {
			addParticle(
					resonance,
					packet.readDouble(), packet.readDouble(), packet.readDouble(),
					packet.readDouble(), packet.readDouble(), packet.readDouble()
			);
		}
	}

	public void render(Camera camera, float tickDelta) {
		BufferBuilder buffer = Tesselator.getInstance().getBuilder(); //Same buffer builder used for vanilla particles

		//Items below copied from particle engine and particle render types
		RenderSystem.setShader(GameRenderer::getParticleShader);
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		RenderSystem.depthMask(true);
		RenderSystem.setShaderTexture(0, PARTICLE_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);

		double camX = camera.getPosition().x;
		double camY = camera.getPosition().y;
		double camZ = camera.getPosition().z;
		Quaternion rot = camera.rotation();
		for (int i = 0; i < count; i++) {
			float relX = (float) (Mth.lerp(tickDelta, positions[3*i], positions[3*i]+velocities[3*i]) - camX);
			float relY = (float) (Mth.lerp(tickDelta, positions[3*i+1], positions[3*i+1]+velocities[3*i+1]) - camY);
			float relZ = (float) (Mth.lerp(tickDelta, positions[3*i+2], positions[3*i+2]+velocities[3*i+2]) - camZ);

			//random ass numbers hope they look good
			int spriteIndex = ((i * 7 - life[i] / 3) % 10 + 10) % 10;
			float v1 = spriteIndex / 10f;
			float v2 = v1 + 0.1f;

			//lazy copy paste below
			Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
			float size = 0.14f;
			for(int k = 0; k < 4; ++k) {
				Vector3f vector3f2 = vector3fs[k];
				vector3f2.transform(rot);
				vector3f2.mul(size);
				vector3f2.add(relX, relY, relZ);
			}

			//add vertices, color is from resonance
			int color = Resonances.getColor(resonances[i]);
			buffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(0, v1).color(color).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(0, v2).color(color).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(1, v2).color(color).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(1, v1).color(color).uv2(LightTexture.FULL_BRIGHT).endVertex();
		}
		Tesselator.getInstance().end();
	}
}
