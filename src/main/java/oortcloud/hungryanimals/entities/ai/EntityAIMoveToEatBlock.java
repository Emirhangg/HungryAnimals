package oortcloud.hungryanimals.entities.ai;

import java.util.ArrayList;

import com.google.common.base.Predicate;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.blocks.ModBlocks;
import oortcloud.hungryanimals.entities.capability.ICapabilityHungryAnimal;
import oortcloud.hungryanimals.entities.capability.ProviderHungryAnimal;
import oortcloud.hungryanimals.entities.food_preferences.FoodPreferenceManager;
import oortcloud.hungryanimals.entities.food_preferences.IFoodPreference;

public class EntityAIMoveToEatBlock extends EntityAIBase {

	private Predicate<Entity> predicate = new Predicate<Entity>() {
		public boolean apply(Entity obj) {
			return obj.getClass() == entity.getClass();
		}
	};

	protected EntityLiving entity;
	protected World worldObj;
	private BlockPos bestPos;
	private double speed;

	protected IFoodPreference<IBlockState> pref;
	protected ICapabilityHungryAnimal capHungry;
	private int delayCounter;
	private static int delay = 100;

	public EntityAIMoveToEatBlock(EntityLiving entity, double speed) {
		this.delayCounter = entity.getRNG().nextInt(delay);
		this.entity = entity;
		this.worldObj = this.entity.worldObj;
		this.speed = speed;
		this.pref = FoodPreferenceManager.getInstance().REGISTRY_BLOCK.get(this.entity.getClass());
		this.capHungry = entity.getCapability(ProviderHungryAnimal.CAP, null);
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (!pref.shouldEat(capHungry)) {
			return false;
		}

		if (this.delayCounter > 0) {
			--this.delayCounter;
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void startExecuting() {
		float herdRadius = 32.0F;
		// find central position of the herd
		// find closest entity in the herd

		BlockPos centralPos = new BlockPos(entity);
		BlockPos closestPos = null;
		double minimumDistanceSq = Double.MAX_VALUE;

		ArrayList<Entity> list = (ArrayList<Entity>) this.worldObj.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().expand(herdRadius, herdRadius, herdRadius), predicate);
		for (Entity e : list) {
			centralPos = centralPos.add(e.getPosition());

			double dist = entity.getPosition().distanceSq(e.getPosition());

			if (dist < minimumDistanceSq) {
				closestPos = e.getPosition();
				minimumDistanceSq = dist;
			}
		}
		double size = list.size()+1;
		centralPos=new BlockPos(centralPos.getX()/size,centralPos.getY()/size,centralPos.getZ()/size);

		// find best block to go
		int searchRadius = 8;
		double bestValue = -Double.MAX_VALUE;
		
		for (int i = -searchRadius; i <= searchRadius; i++) {
			for (int j = -searchRadius; j <= searchRadius; j++) {
				for (int k = -searchRadius; k <= searchRadius; k++) {
					double value;
					BlockPos iPos = entity.getPosition().add(i, j, k);
					if (closestPos == null) {
						value = this.getBlockPathWeight(iPos) * (1 + entity.getRNG().nextDouble()) * centralizationFunction(Math.sqrt(centralPos.distanceSq(iPos)));
					} else {
						value = this.getBlockPathWeight(iPos) * (1 + entity.getRNG().nextDouble()) * centralizationFunction(Math.sqrt(centralPos.distanceSq(iPos)))
								* (0.1 * (Math.sqrt(closestPos.distanceSq(iPos))) + 1);
					}
					
					if (value > bestValue) {
						bestValue = value;
						bestPos=iPos;
					}
				}
			}
		}

		entity.getNavigator().tryMoveToXYZ(bestPos.getX(), bestPos.getY(), bestPos.getZ(), this.speed);
	}

	@Override
	public boolean continueExecuting() {
		IBlockState block = this.worldObj.getBlockState(bestPos);
		if (!this.pref.canEat(capHungry, block)) {
			this.entity.getNavigator().clearPathEntity();
			return false;
		}
		if (entity.getNavigator().noPath()) {
			float distanceSq = 2;
			if (bestPos.distanceSqToCenter(entity.posX, entity.posY, entity.posZ) <= distanceSq) {
				if (this.worldObj.getGameRules().getBoolean("mobGriefing")) {
					this.worldObj.setBlockToAir(bestPos);
				}
				eatBlockBonus(block);
			}
			return false;
		}
		return true;
	}

	@Override
	public void resetTask() {
		bestPos=null;
		delayCounter = delay;
	}

	private double centralizationFunction(double R) {
		double k = 0.5;
		if (R > 32)
			return 0;
		return -k * (R - 32) + 1;
	}
	
	private double getBlockPathWeight(BlockPos pos) {
		IBlockState state = this.worldObj.getBlockState(pos);
		if (state.getBlock() == ModBlocks.excreta) {
			return -1.0;
		} else if (pref.canEat(capHungry, state)) {
			return pref.getHunger(state);
		} else {
			return 1.0;
		}
	}
	
	public void eatBlockBonus(IBlockState block) {
		if (block == null)
			return;
		double hunger = pref.getHunger(block);
		capHungry.addHunger(hunger);
	}


}
