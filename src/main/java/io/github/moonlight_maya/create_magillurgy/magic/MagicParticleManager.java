package io.github.moonlight_maya.create_magillurgy.magic;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;

import com.mojang.blaze3d.vertex.Tesselator;

import com.mojang.math.Quaternion;

import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class MagicParticleManager {

	//Number of ticks that a magic particle will stick around for before despawning
	//5 minutes
	private static final short LIFETIME = 20 * 60 * 5;

	private int capacity;
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
				count--;
				//Remove this particle by copying the last particle into it
				//and decrementing the count. the order of particles in memory doesn't
				//matter.
				positions[3*i] = positions[count *3];
				positions[3*i+1] = positions[count *3+1];
				positions[3*i+2] = positions[count *3+2];
				velocities[3*i] = velocities[count *3];
				velocities[3*i+1] = velocities[count *3+1];
				velocities[3*i+2] = velocities[count *3+2];
				life[i] = life[count];
				resonances[i] = resonances[count];
				i--;
			} else {
				positions[3*i] += velocities[3*i];
				positions[3*i+1] += velocities[3*i+1];
				positions[3*i+2] += velocities[3*i+2];
				velocities[3*i+1] = Mth.clamp(velocities[3*i+1] + 0.001, 0, 0.05);
				velocities[3*i] *= 0.97;
				velocities[3*i+2] *= 0.97;
			}
		}
	}

	public void addParticle(int resonance, Vec3 pos, Vec3 vel) {
		if (count == capacity) {
			//resize
			throw new UnsupportedOperationException("TODO");
		} else {
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
	}

	public void render(Camera camera, float tickDelta) {
		BufferBuilder buffer = Tesselator.getInstance().getBuilder(); //Same buffer builder used for vanilla particles
		double camX = camera.getPosition().x;
		double camY = camera.getPosition().y;
		double camZ = camera.getPosition().z;
		Quaternion rot = camera.rotation();
		for (int i = 0; i < count; i++) {
			float relX = (float) (Mth.lerp(tickDelta, positions[3*i], positions[3*i]+velocities[3*i]) - camX);
			float relY = (float) (Mth.lerp(tickDelta, positions[3*i+1], positions[3*i+1]+velocities[3*i+1]) - camY);
			float relZ = (float) (Mth.lerp(tickDelta, positions[3*i+2], positions[3*i+2]+velocities[3*i+2]) - camZ);
			//lazy copy paste below
			Vector3f[] vector3fs = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
			float j = 0.25f;
			for(int k = 0; k < 4; ++k) {
				Vector3f vector3f2 = vector3fs[k];
				vector3f2.transform(rot);
				vector3f2.mul(j);
				vector3f2.add(relX, relY, relZ);
			}
			//add vertices
			buffer.vertex(vector3fs[0].x(), vector3fs[0].y(), vector3fs[0].z()).uv(0, 0).color(0.25f, 0.5f, 0.75f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[1].x(), vector3fs[1].y(), vector3fs[1].z()).uv(0, 1).color(0.5f, 0.25f, 0.75f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[2].x(), vector3fs[2].y(), vector3fs[2].z()).uv(1, 1).color(0.5f, 0.75f, 0.25f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
			buffer.vertex(vector3fs[3].x(), vector3fs[3].y(), vector3fs[3].z()).uv(1, 0).color(0.75f, 0.5f, 0.25f, 1.0f).uv2(LightTexture.FULL_BRIGHT).endVertex();
		}
	}
}
