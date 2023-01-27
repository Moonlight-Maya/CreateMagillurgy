package io.github.moonlight_maya.create_magillurgy.client;

import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import io.github.moonlight_maya.create_magillurgy.magic.particles.ClientParticleManager;
import io.github.moonlight_maya.create_magillurgy.magic.particles.MagicParticleManager;
import io.github.moonlight_maya.create_magillurgy.magic.particles.ServerParticleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MagillurgyAddonClient implements ClientModInitializer {

	public static final ClientParticleManager CLIENT_PARTICLES = new ClientParticleManager(1000);

	@Override
	public void onInitializeClient() {
		MagillurgyBlockPartials.init();
		MagillurgyRenderTypes.init();

		ClientPlayNetworking.registerGlobalReceiver(ServerParticleManager.PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			CLIENT_PARTICLES.handleParticlePacket(buf);
		});

		ClientTickEvents.START_CLIENT_TICK.register(client -> CLIENT_PARTICLES.tick());
	}
}
