package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.simibubi.create.foundation.tileEntity.SmartTileEntity;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.BehaviourType;

public class VaporizationBehaviour extends TileEntityBehaviour {
	public VaporizationBehaviour(SmartTileEntity te) {
		super(te);
	}
	@Override
	public BehaviourType<?> getType() {
		return null;
	}
}
