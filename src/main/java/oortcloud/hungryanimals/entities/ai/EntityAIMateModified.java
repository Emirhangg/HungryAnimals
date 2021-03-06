package oortcloud.hungryanimals.entities.ai;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.JsonUtils;
import net.minecraft.world.World;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.entities.ai.handler.AIContainer;
import oortcloud.hungryanimals.entities.ai.handler.AIFactory;
import oortcloud.hungryanimals.entities.attributes.ModAttributes;
import oortcloud.hungryanimals.entities.capability.ICapabilityHungryAnimal;
import oortcloud.hungryanimals.entities.capability.ICapabilityTamableAnimal;
import oortcloud.hungryanimals.entities.capability.ProviderHungryAnimal;
import oortcloud.hungryanimals.entities.capability.ProviderTamableAnimal;

public class EntityAIMateModified extends EntityAIBase {
	private EntityAnimal animal;
	private ICapabilityHungryAnimal theAnimalCapHungry;
	private ICapabilityTamableAnimal theAnimalCapTamable;
	World theWorld;
	private EntityAnimal targetMate;
	/** Delay preventing a baby from spawning immediately when two mate-able animals find each other. */
	int spawnBabyDelay;
	/** The speed the creature moves at during mating behavior. */
	double moveSpeed;

	public EntityAIMateModified(EntityAnimal animal, double speed) {
		this.animal = animal;
		this.theWorld = animal.getEntityWorld();
		this.moveSpeed = speed;
		this.theAnimalCapHungry = animal.getCapability(ProviderHungryAnimal.CAP, null);
		this.theAnimalCapTamable = animal.getCapability(ProviderTamableAnimal.CAP, null);
		this.setMutexBits(3);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (!this.animal.isInLove()) {
			return false;
		} else {
			this.targetMate = this.getNearbyMate();
			return this.targetMate != null;
		}
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean continueExecuting() {
		return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
	}

	/**
	 * Resets the task
	 */
	public void resetTask() {
		this.targetMate = null;
		this.spawnBabyDelay = 0;
	}

	/**
	 * Updates the task
	 */
	public void updateTask() {
		this.animal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float) this.animal.getVerticalFaceSpeed());
		this.animal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
		++this.spawnBabyDelay;
		if (this.spawnBabyDelay >= 60 && this.animal.getDistanceSq(this.targetMate) < 9.0D) {
			this.spawnBaby();
		}
	}

	private EntityAnimal getNearbyMate() {
		List<EntityAnimal> list = this.theWorld.<EntityAnimal>getEntitiesWithinAABB(this.animal.getClass(), this.animal.getEntityBoundingBox().grow(8.0D));
		double d0 = Double.MAX_VALUE;
		EntityAnimal entityanimal = null;

		for (EntityAnimal entityanimal1 : list) {
			if (this.animal.canMateWith(entityanimal1) && this.animal.getDistanceSq(entityanimal1) < d0) {
				entityanimal = entityanimal1;
				d0 = this.animal.getDistanceSq(entityanimal1);
			}
		}

		return entityanimal;
	}

	/**
	 * Spawns a baby animal of the same type.
	 */
	private void spawnBaby() {
		// Get Capability
		ICapabilityHungryAnimal targetMateCapHungry = this.targetMate.getCapability(ProviderHungryAnimal.CAP, null);
		ICapabilityTamableAnimal targetMateCapTamable = this.targetMate.getCapability(ProviderTamableAnimal.CAP, null);

		// Create Child 1
		EntityAgeable entityageable = this.animal.createChild(this.targetMate);

		// Check Validity
		boolean createChildDeclared = false;
		try {
			Method createChild = animal.getClass().getDeclaredMethod("createChild", EntityAgeable.class);
			if (createChild != null)
				createChildDeclared = true;
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		}

		// Create Child 2
		if (!createChildDeclared || entityageable == null) {
			entityageable = createChild();
		}

		if (entityageable != null) {
			ICapabilityTamableAnimal childTamable = entityageable.getCapability(ProviderTamableAnimal.CAP, null);
			ICapabilityHungryAnimal childHungry = entityageable.getCapability(ProviderHungryAnimal.CAP, null);
			
			// Pay Hunger
			double weight_child = entityageable.getEntityAttribute(ModAttributes.hunger_weight_normal_child).getAttributeValue();
			targetMateCapHungry.addWeight(-weight_child/2);
			theAnimalCapHungry.addWeight(-weight_child/2);
			
			childHungry.setWeight(weight_child);
			childTamable.setTaming((theAnimalCapTamable.getTaming() + targetMateCapTamable.getTaming()) / 2.0);

			EntityPlayerMP entityplayermp = this.animal.getLoveCause();

			if (entityplayermp == null && this.targetMate.getLoveCause() != null) {
				entityplayermp = this.targetMate.getLoveCause();
			}

			if (entityplayermp != null) {
				entityplayermp.addStat(StatList.ANIMALS_BRED);
				CriteriaTriggers.BRED_ANIMALS.trigger(entityplayermp, this.animal, this.targetMate, entityageable);
			}

			int animalDelay = (int) animal.getEntityAttribute(ModAttributes.child_delay).getAttributeValue();
			int targetMateDelay = (int) targetMate.getEntityAttribute(ModAttributes.child_delay).getAttributeValue();
			int childGrowingLength = (int) entityageable.getEntityAttribute(ModAttributes.child_growing_length).getAttributeValue();
			this.animal.setGrowingAge(animalDelay);
			this.targetMate.setGrowingAge(targetMateDelay);
			this.animal.resetInLove();
			this.targetMate.resetInLove();
			entityageable.setGrowingAge(-childGrowingLength);
			entityageable.setLocationAndAngles(this.animal.posX, this.animal.posY, this.animal.posZ, 0.0F, 0.0F);
			this.theWorld.spawnEntity(entityageable);
			Random random = this.animal.getRNG();

			for (int i = 0; i < 7; ++i) {
				double d0 = random.nextGaussian() * 0.02D;
				double d1 = random.nextGaussian() * 0.02D;
				double d2 = random.nextGaussian() * 0.02D;
				double d3 = random.nextDouble() * (double) this.animal.width * 2.0D - (double) this.animal.width;
				double d4 = 0.5D + random.nextDouble() * (double) this.animal.height;
				double d5 = random.nextDouble() * (double) this.animal.width * 2.0D - (double) this.animal.width;
				this.theWorld.spawnParticle(EnumParticleTypes.HEART, this.animal.posX + d3, this.animal.posY + d4, this.animal.posZ + d5, d0, d1, d2,
						new int[0]);
			}

			if (this.theWorld.getGameRules().getBoolean("doMobLoot")) {
				this.theWorld.spawnEntity(new EntityXPOrb(this.theWorld, this.animal.posX, this.animal.posY, this.animal.posZ, random.nextInt(7) + 1));
			}
		} else {
			this.animal.resetInLove();
			this.targetMate.resetInLove();
		}
	}

	public EntityAnimal createChild() {
		Constructor<? extends EntityAnimal> constructor;
		try {
			constructor = animal.getClass().getConstructor(World.class);
			EntityAnimal baby;
			try {
				baby = (EntityAnimal) constructor.newInstance(theWorld);
				return baby;
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void parse(JsonElement jsonEle, AIContainer aiContainer) {
		if (! (jsonEle instanceof JsonObject)) {
			HungryAnimals.logger.error("AI Mate must be an object.");
			throw new JsonSyntaxException(jsonEle.toString());
		}
		
		JsonObject jsonObject = (JsonObject)jsonEle ;
		
		float speed = JsonUtils.getFloat(jsonObject, "speed");
		
		AIFactory factory =  (entity) -> new EntityAIMateModified(entity, speed);
		aiContainer.getTask().after(EntityAISwimming.class)
		                     .before(EntityAIMoveToTrough.class)
		                     .before(EntityAITemptIngredient.class)
		                     .before(EntityAITemptEdibleItem.class)
		                     .before(EntityAIMoveToEatItem.class)
		                     .before(EntityAIMoveToEatBlock.class)
		                     .before(EntityAIFollowParent.class)
		                     .put(factory);
	}
}