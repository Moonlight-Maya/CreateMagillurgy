package io.github.moonlight_maya.create_magillurgy.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import com.mojang.blaze3d.vertex.VertexFormat;

import io.github.fabricators_of_create.porting_lib.mixin.client.accessor.RenderTypeAccessor;
import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class MagillurgyRenderTypes extends RenderStateShard {

	//protected fields :catstare:
	public MagillurgyRenderTypes(String name, Runnable setupState, Runnable clearState) {
		super(name, setupState, clearState);
	}

	private static final RenderType CUTOUT_NO_CULL = RenderTypeAccessor.port_lib$create(MagillurgyAddon.ID + ":" + "cutout_no_cull",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			256, true, false, RenderType.CompositeState.builder()
					.setLightmapState(LIGHTMAP)
					.setShaderState(RENDERTYPE_CUTOUT_SHADER)
					.setTextureState(BLOCK_SHEET)
					.setCullState(NO_CULL) //The key: remove culling here
					.createCompositeState(true)
	);

	public static RenderType cutoutNoCull() {
		return CUTOUT_NO_CULL;
	}

	//Load class
	public static void init() {}
}
