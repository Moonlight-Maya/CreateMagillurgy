package io.github.moonlight_maya.create_magillurgy.magic;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MagicParticleManager {

	//Number of ticks that a magic particle will stick around for before disappearing
	//just 15 seconds, they're intended to generally be created and collected quickly
	//Since they can be perhaps unintentionally created by a number of different actions,
	//it's good to not have them stick around too long.
	private static final short LIFETIME = 20 * 15;

	private int capacity; //Capacity for particles before the arrays need to be resized
	private int count;
	//Multiple arrays because project valhalla doesnt exist yet
	private int[] resonances;
	private short[] life; //remaining lifetime in ticks, if it hits zero it will despawn
	private double[] positions;
	private double[] velocities;

	//InitialSize is the initial number of particles the system can hold
	//Arrays double in size when this reaches capacity
	public MagicParticleManager(int initialSize) {
		count = 0;
		capacity = initialSize;
		resonances = new int[initialSize];
		life = new short[initialSize];
		positions = new double[initialSize * 3];
		velocities = new double[initialSize * 3];
	}

	public void tick() {
		for (int i = 0; i < count; i++) {
			if (--life[i] == 0) {
				deleteParticle(i--);
			} else {
				updateParticle(i);
			}
		}
	}

	public void addParticle(int resonance, Vec3 pos, Vec3 vel) {
		if (count == capacity)
			resize();
		resonances[count] = resonance;
		life[count] = LIFETIME;
		positions[3*count] = pos.x;
		positions[3*count+1] = pos.y;
		positions[3*count+2] = pos.z;
		velocities[3*count] = vel.x;
		velocities[3*count+1] = vel.y;
		velocities[3*count+2] = vel.z;
		count++;
	}

	private void deleteParticle(int index) {
		count--;
		//Remove this particle by copying the last particle into it
		//and decrementing the count. the order of particles in memory
		//shouldn't matter.
		positions[3*index] = positions[count*3];
		positions[3*index+1] = positions[count*3+1];
		positions[3*index+2] = positions[count*3+2];
		velocities[3*index] = velocities[count*3];
		velocities[3*index+1] = velocities[count*3+1];
		velocities[3*index+2] = velocities[count*3+2];
		life[index] = life[count];
		resonances[index] = resonances[count];
	}

	private void updateParticle(int index) {
		positions[3*index] += velocities[3*index];
		positions[3*index+1] += velocities[3*index+1];
		positions[3*index+2] += velocities[3*index+2];
		velocities[3*index+1] = Mth.clamp(velocities[3*index+1] + 0.001, 0, 0.05);
		velocities[3*index] *= 0.97;
		velocities[3*index+2] *= 0.97;
	}

	private void resize() {
		//Create each new array with double the size, copy values, and re-assign
		int[] newResonances = new int[capacity * 2];
		System.arraycopy(resonances, 0, newResonances, 0, capacity);
		resonances = newResonances;

		short[] newLife = new short[capacity * 2];
		System.arraycopy(life, 0, newLife, 0, capacity);
		life = newLife;

		double[] newPositions = new double[capacity * 6];
		System.arraycopy(positions, 0, newPositions, 0, capacity * 3);
		positions = newPositions;

		double[] newVelocities = new double[capacity * 6];
		System.arraycopy(velocities, 0, newVelocities, 0, capacity * 3);
		velocities = newVelocities;

		capacity *= 2;
	}

	//Below lies rendering code!

	private static final ResourceLocation PARTICLE_TEXTURE = new ResourceLocation(MagillurgyAddon.ID, "textures/magic.png");

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
			//add vertices
			buffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(0, v1).color(0.0f, 1.0f, 1.0f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(0, v2).color(0.0f, 1.0f, 1.0f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(1, v2).color(0.0f, 1.0f, 1.0f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(1, v1).color(0.0f, 1.0f, 1.0f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
		}
		Tesselator.getInstance().end();
	}
}
