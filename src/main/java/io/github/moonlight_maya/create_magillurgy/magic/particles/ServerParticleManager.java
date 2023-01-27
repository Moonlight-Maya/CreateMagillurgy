package io.github.moonlight_maya.create_magillurgy.magic.particles;

import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

/**
 * Contains methods related to sending particle packets to clients
 */
public class ServerParticleManager extends MagicParticleManager {
	public ServerParticleManager(int initialSize) {
		super(initialSize);
	}

	public static final ResourceLocation PARTICLE_PACKET_ID = MagillurgyAddon.id("particles");

	private IntArrayList queuedResonances;
	private DoubleArrayList queuedPositions;
	private DoubleArrayList queuedVelocities;

	@Override
	public void addParticle(int resonance, double x, double y, double z, double xVel, double yVel, double zVel) {
		if (queuedPositions == null) {
			throw new IllegalStateException("Attempt to add particle without starting a particle burst");
		}
		queuedPositions.add(x);
		queuedPositions.add(y);
		queuedPositions.add(z);
		queuedVelocities.add(xVel);
		queuedVelocities.add(yVel);
		queuedVelocities.add(zVel);
		queuedResonances.add(resonance);
	}

	public void beginParticleBurst(int expectedSize) {
		queuedResonances = new IntArrayList(expectedSize);
		queuedPositions = new DoubleArrayList(expectedSize * 3);
		queuedVelocities = new DoubleArrayList(expectedSize * 3);
	}

	public void endParticleBurst(Collection<ServerPlayer> playersToNotify) {
		FriendlyByteBuf buf = PacketByteBufs.create();
		//Write resonances. Length of this array will be used to determine length of the other two, since there is no built-in double array.
		buf.writeVarIntArray(queuedResonances.toIntArray());
		for (int i = 0; i < queuedResonances.size()*3; i+=3) {
			//Write pos
			buf.writeDouble(queuedPositions.getDouble(i));
			buf.writeDouble(queuedPositions.getDouble(i+1));
			buf.writeDouble(queuedPositions.getDouble(i+2));
			//Write vel
			buf.writeDouble(queuedVelocities.getDouble(i));
			buf.writeDouble(queuedVelocities.getDouble(i+1));
			buf.writeDouble(queuedVelocities.getDouble(i+2));
		}
		queuedResonances = null;
		queuedPositions = null;
		queuedVelocities = null;
		//Send the packet to all players specified
		for (ServerPlayer player : playersToNotify)
			ServerPlayNetworking.send(player, PARTICLE_PACKET_ID, buf);
	}

}
