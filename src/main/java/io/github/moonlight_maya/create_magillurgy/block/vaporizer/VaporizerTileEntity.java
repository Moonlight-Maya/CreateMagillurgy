package io.github.moonlight_maya.create_magillurgy.block.vaporizer;

import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;

import com.simibubi.create.content.contraptions.fluids.actors.FillingBySpout;
import com.simibubi.create.content.contraptions.relays.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.tileEntity.TileEntityBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.belt.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.foundation.tileEntity.behaviour.fluid.SmartFluidTankBehaviour;

import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.util.EnvExecutor;
import io.github.fabricators_of_create.porting_lib.util.FluidStack;
import io.github.moonlight_maya.create_magillurgy.MagillurgyAddon;
import io.github.moonlight_maya.create_magillurgy.client.MagillurgyAddonClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;

import net.minecraft.world.level.material.Fluids;

import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult.HOLD;
import static com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult.PASS;
import static com.simibubi.create.foundation.tileEntity.behaviour.belt.BeltProcessingBehaviour.ProcessingResult.REMOVE;

public class VaporizerTileEntity extends KineticTileEntity implements SidedStorageBlockEntity {

	private SmartFluidTankBehaviour upperTank; //holds 1/2 a bucket

	//Don't need a full-blown storage for something this small, only interacted with by
	//the machine internals themselves
	private FluidStack lowerTankFluidStack;

	//If -1, the machine is idle
	//If 0, it just stopped firing, so we should do the conversion
	//If >0, the firing animation is still happening, so just decrement and move on
	private int firingTicks;

	//40500
	public static final long UPPER_TANK_CAPACITY = FluidConstants.fromBucketFraction(1, 2);
	//Also the "capacity" of the lower tank (currently 10125, cannot be divided evenly in 2 again)
	public static final long COST_PER_OPERATION = FluidConstants.fromBucketFraction(1, 8);

	private static final int TICKS_TO_FIRE = 3;
	//chosen arbitrarily-ish, at 256 rpm it will take 10 ticks to charge
	//need to be careful so fluid doesn't get voided as a result of this
	private static final long CHARGE_PER_RPM_PER_TICK = 4;

	public VaporizerTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		firingTicks = -1;
		lowerTankFluidStack = new FluidStack(FluidVariant.blank(), 0);
	}

	public FluidStack fluidInUpperTank() {
		return upperTank.getPrimaryHandler().getFluid();
	}

	public FluidStack fluidInLowerTank() {
		return lowerTankFluidStack;
	}

	@Override
	public void addBehaviours(List<TileEntityBehaviour> behaviours) {
		upperTank = SmartFluidTankBehaviour.single(this, UPPER_TANK_CAPACITY);
		behaviours.add(upperTank);

		BeltProcessingBehaviour beltProcessing = new BeltProcessingBehaviour(this)
				.whenItemEnters(this::onItemReceived)
				.whileItemHeld(this::whenItemHeld);
		behaviours.add(beltProcessing);

//		VaporizationBehaviour vaporization = new VaporizationBehaviour(this);
//		behaviours.add(vaporization);
	}

	protected BeltProcessingBehaviour.ProcessingResult onItemReceived(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if (handler.tileEntity.isVirtual())
			return PASS;
		if (!transported.stack.is(Items.NETHER_STAR))
			return PASS;
		return HOLD;
	}

	protected BeltProcessingBehaviour.ProcessingResult whenItemHeld(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if (firingTicks > 0) //If already firing, wait until done firing to release item.
			return HOLD;
		if (!transported.stack.is(Items.NETHER_STAR)) //If the item is invalid, let it pass.
			return PASS;
		if (lowerTankFluidStack.getAmount() < COST_PER_OPERATION) //If we don't have enough power charged up yet, wait until we do.
			return HOLD;

		//We just finished firing, so time to blow up the item.
		if (firingTicks == 0) {
			//Destroy the item.
			if (transported.stack.getCount() > 1) {
				transported.stack.shrink(1);
				TransportedItemStack held = null;
				TransportedItemStack result = transported.copy();
				result.stack = transported.stack.copy();
				result.stack.setCount(1);
				if (!transported.stack.isEmpty())
					held = transported.copy();
				handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(List.of(result), held));
			} else {
				handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.removeItem());
			}
			//Reset the state of the tile entity.
			lowerTankFluidStack = new FluidStack(FluidVariant.blank(), 0);

			//All that dealt with, spawn the particles.
			//Jank temporary code, just to visualize client side.
			EnvExecutor.runWhenOn(EnvType.CLIENT, () -> () -> {
				Vec3 position = Vec3.upFromBottomCenterOf(getBlockPos().below(2), 0.6);
				for (int i = 0; i < 100; i++) {
					Vec3 particleVel = new Vec3(Math.random() * 0.1 - 0.05, Math.random()*0.01, Math.random() * 0.1 - 0.05);
					MagillurgyAddonClient.CLIENT_PARTICLES.addParticle(0, position, particleVel);
				}
			});
			notifyUpdate();
			return HOLD;
		}

		//If we made it to here, then we have a valid item below the vaporizer, and it's fully charged and ready.
		//Time to begin firing.
		firingTicks = TICKS_TO_FIRE;
		notifyUpdate();
		return HOLD;
	}

	@Override
	protected void write(CompoundTag compound, boolean clientPacket) {
		super.write(compound, clientPacket);
		compound.putInt("FiringTicks", firingTicks);
		compound.putLong("LowerTankFluidAmount", lowerTankFluidStack.getAmount());
//		compound.putInt("LowerTankFluidResonance", 0);
	}

	@Override
	protected void read(CompoundTag compound, boolean clientPacket) {
		super.read(compound, clientPacket);
		firingTicks = compound.getInt("FiringTicks");
		long fluidAmount = compound.getLong("LowerTankFluidAmount");
		lowerTankFluidStack = new FluidStack(Fluids.WATER, fluidAmount);
	}

	@Override
	public void tick() {
		super.tick();

		//If currently firing or just finished firing, decrement the firing ticks
		if (firingTicks >= 0) {
			firingTicks--;
		}

		if (firingTicks == -1) {
			//If not firing, and lower tank is not full, try to add to lower tank
			if (lowerTankFluidStack.getAmount() < COST_PER_OPERATION) {
				long amountInUpperTank = fluidInUpperTank().getAmount(); //Can only transfer as much as you have
				long missingFluid = COST_PER_OPERATION - lowerTankFluidStack.getAmount(); //Can only transfer as much as is missing
				long transferSpeed = (long) (CHARGE_PER_RPM_PER_TICK * Math.abs(getSpeed())); //Can only charge as fast as you're spinning the gear

				//Amount actually transferred is the minimum of all three
				long amountTransferred = Math.min(missingFluid, Math.min(transferSpeed, amountInUpperTank));
				//Perform transfer by growing and shrinking fluid stacks
				TransactionContext transactionContext = TransferUtil.getTransaction();
				long actuallyTransferred = upperTank.getCapability().extract(FluidVariant.of(Fluids.WATER), amountTransferred, transactionContext);
				if (amountTransferred != actuallyTransferred) {
					MagillurgyAddon.LOGGER.error("Error with fluid processing within vaporizer!");
					throw new RuntimeException();
				}
				lowerTankFluidStack.grow(actuallyTransferred);
			}
		}
	}

	public float getExtension(float tickDelta) {
		float ticksLeftFiring = Math.max(firingTicks-tickDelta, 0);
		if (ticksLeftFiring > 0)
			return ticksLeftFiring / TICKS_TO_FIRE;
		return (float) lowerTankFluidStack.getAmount() / COST_PER_OPERATION;
	}

	//Fluid storage accessible only from above
	@Override
	public @Nullable Storage<FluidVariant> getFluidStorage(Direction side) {
		if (side != Direction.UP)
			return null;
		return upperTank.getCapability();
	}
}
