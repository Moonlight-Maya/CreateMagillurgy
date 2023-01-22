package io.github.moonlight_maya.create_magillurgy;

import com.simibubi.create.events.ClientEvents;

import io.github.moonlight_maya.create_magillurgy.magic.MagicParticleManager;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MagillurgyAddon implements ModInitializer {
	public static final String ID = "create-magillurgy";
	public static final String NAME = "Create: Magillurgy";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	public static final MagicParticleManager TEST_PARTICLES = new MagicParticleManager(10000);

	@Override
	public void onInitialize() {
		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);

		ClientTickEvents.START_CLIENT_TICK.register(client -> {
			TEST_PARTICLES.tick();
		});

	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}