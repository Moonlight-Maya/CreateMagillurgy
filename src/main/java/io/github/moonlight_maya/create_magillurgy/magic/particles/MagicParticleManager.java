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

	protected int capacity; //Capacity for particles before the arrays need to be resized
	protected int count;
	//Multiple arrays because project valhalla doesnt exist yet
	protected int[] resonances;
	protected short[] life; //remaining lifetime in ticks, if it hits zero it will despawn
	protected double[] positions;
	protected double[] velocities;

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

	public static Vec3 randomVelocityHelper(double speed) {
		Vec3 vel = new Vec3(Math.random(), Math.random(), Math.random()).scale(2*speed).subtract(speed,speed,speed);
		while (vel.lengthSqr() > speed * speed)
			vel = new Vec3(Math.random(), Math.random(), Math.random()).scale(2*speed).subtract(speed,speed,speed);
		return vel;
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

	//Adds a particle directly, without going through any networking
	protected void addParticle(int resonance, double x, double y, double z, double xVel, double yVel, double zVel) {
		if (count == capacity)
			resize();
		resonances[count] = resonance;
		life[count] = LIFETIME;
		positions[3*count] = x;
		positions[3*count+1] = y;
		positions[3*count+2] = z;
		velocities[3*count] = xVel;
		velocities[3*count+1] = yVel;
		velocities[3*count+2] = zVel;
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
		double TARGET_Y_VEL = 0.05;
		velocities[3*index+1] = Mth.lerp(0.01, velocities[3*index+1], TARGET_Y_VEL);
		velocities[3*index] *= 0.985;
		velocities[3*index+2] *= 0.985;
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


}
