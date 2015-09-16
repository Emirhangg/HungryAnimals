package oortcloud.hungryanimals.entities.ai;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.blocks.BlockTrough;
import oortcloud.hungryanimals.blocks.ModBlocks;
import oortcloud.hungryanimals.core.network.PacketTileEntityClient;
import oortcloud.hungryanimals.entities.properties.ExtendedPropertiesHungryAnimal;
import oortcloud.hungryanimals.tileentities.TileEntityTrough;

public class EntityAIMoveToTrough extends EntityAIBase {

	private EntityAnimal entity;
	private ExtendedPropertiesHungryAnimal property;
	private double speed;
	private World world;
	public BlockPos pos;
	private int delayCounter;

	public EntityAIMoveToTrough(EntityAnimal entity, ExtendedPropertiesHungryAnimal property, double speed) {
		this.entity = entity;
		this.property = property;
		this.world = this.entity.worldObj;
		this.speed = speed;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (pos == null)
			return false;
		
		if (this.delayCounter > 0) {
			--this.delayCounter;
			return false;
		} else {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == ModBlocks.trough) {
				TileEntity temp = ((BlockTrough) state.getBlock()).getTileEntity(world, pos);
				if (property.taming >= 1 && temp != null && temp instanceof TileEntityTrough) {
					TileEntityTrough trough = (TileEntityTrough) temp;
					return trough.stack != null && this.property.canEatFood(trough.stack);
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	@Override
	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), this.speed);
	}

	@Override
	public boolean continueExecuting() {
		float distSq = 2;
		if (entity.getPosition().distanceSq(pos) <= distSq) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() == ModBlocks.trough) {
				TileEntity tileEntity = ((BlockTrough) state.getBlock()).getTileEntity(world, pos);
				if (tileEntity != null && tileEntity instanceof TileEntityTrough) {
					TileEntityTrough trough = (TileEntityTrough) tileEntity;
					if (trough.stack != null && this.property.canEatFood(trough.stack)) {
						property.eatFoodBonus(trough.stack);
						trough.stack.stackSize--;
						if (trough.stack.stackSize == 0)
							trough.stack = null;

						PacketTileEntityClient msg0 = new PacketTileEntityClient(0, this.world.provider.getDimensionId(), pos);
						msg0.setItemStack(trough.stack);
						HungryAnimals.simpleChannel.sendToAll(msg0);
					}
				}
			}
			return false;
		}
		if (entity.getNavigator().noPath()) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void resetTask() {
		delayCounter = 100;
	}
	
}
