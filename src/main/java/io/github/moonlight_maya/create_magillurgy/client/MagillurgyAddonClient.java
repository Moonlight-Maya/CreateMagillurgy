package io.github.moonlight_maya.create_magillurgy.client;

import io.github.moonlight_maya.create_magillurgy.magic.MagicParticleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class MagillurgyAddonClient implements ClientModInitializer {

	public static final MagicParticleManager CLIENT_PARTICLES = new MagicParticleManager(1000);

	@Override
	public void onInitializeClient() {
		MagillurgyBlockPartials.init();
		MagillurgyRenderTypes.init();

		ClientTickEvents.START_CLIENT_TICK.register(client -> CLIENT_PARTICLES.tick());
	}
}
