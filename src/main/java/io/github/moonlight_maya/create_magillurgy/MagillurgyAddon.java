package io.github.moonlight_maya.create_magillurgy;

import com.simibubi.create.foundation.data.CreateRegistrate;

import io.github.moonlight_maya.create_magillurgy.magic.particles.MagicParticleManager;

import io.github.moonlight_maya.create_magillurgy.magic.particles.ServerParticleManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.simibubi.create.Create;

import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;

public class MagillurgyAddon implements ModInitializer {
	public static final String ID = "create-magillurgy";
	public static final String NAME = "Create: Magillurgy";
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);
	public static final ServerParticleManager SERVER_PARTICLES = new ServerParticleManager(10000);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Create addon mod [{}] is loading alongside Create [{}]!", NAME, Create.VERSION);
		LOGGER.info(EnvExecutor.unsafeRunForDist(
				() -> () -> "{} is accessing Porting Lib from the client!",
				() -> () -> "{} is accessing Porting Lib from the server!"
		), NAME);

		MagillurgyBlocks.register();
		MagillurgyTileEntities.register();

		REGISTRATE.register(); //necessary step on fabric

		ServerTickEvents.END_SERVER_TICK.register(server -> SERVER_PARTICLES.tick());
	}

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
