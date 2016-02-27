package oortcloud.hungryanimals.entities.properties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import oortcloud.hungryanimals.blocks.BlockExcreta;
import oortcloud.hungryanimals.blocks.ModBlocks;
import oortcloud.hungryanimals.configuration.util.HashBlockState;
import oortcloud.hungryanimals.configuration.util.HashItemType;
import oortcloud.hungryanimals.configuration.util.ValueDropMeat;
import oortcloud.hungryanimals.configuration.util.ValueDropRandom;
import oortcloud.hungryanimals.configuration.util.ValueDropRare;
import oortcloud.hungryanimals.entities.ai.EntityAIAvoidPlayer;
import oortcloud.hungryanimals.entities.ai.EntityAIMateModified;
import oortcloud.hungryanimals.entities.ai.EntityAIMoveToEatBlock;
import oortcloud.hungryanimals.entities.ai.EntityAIMoveToEatItem;
import oortcloud.hungryanimals.entities.ai.EntityAIMoveToTrough;
import oortcloud.hungryanimals.entities.ai.EntityAITemptEdibleItem;
import oortcloud.hungryanimals.entities.properties.handler.HungryAnimalManager;
import oortcloud.hungryanimals.entities.properties.handler.AnimalCharacteristic;
import oortcloud.hungryanimals.entities.properties.handler.ModAttributes;
import oortcloud.hungryanimals.potion.ModPotions;

public class ExtendedPropertiesHungryAnimal implements IExtendedEntityProperties {

	public static String key = "ExtendedPropertiesHungryAnimal";

	public HashMap<HashItemType, Double> hunger_food = new HashMap<HashItemType, Double>();
	public HashMap<HashBlockState, Double> hunger_block = new HashMap<HashBlockState, Double>();
	public ArrayList<ValueDropMeat> drop_meat = new ArrayList<ValueDropMeat>();
	public ArrayList<ValueDropRandom> drop_random = new ArrayList<ValueDropRandom>();
	public ArrayList<ValueDropRare> drop_rare = new ArrayList<ValueDropRare>();

	public double taming_factor = 0.998;

	public EntityAnimal entity;
	public World worldObj;
	public double hunger;
	public double excretion;
	public double taming;
	public EntityAIMoveToTrough ai_moveToFoodbox;
	//public EntityAICrank ai_crank;

	
	
	public void acceptProperty() {
		HungryAnimalManager.getInstance().applyAttributes(this);
		entity.setHealth(this.entity.getMaxHealth());
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		NBTTagCompound tag = new NBTTagCompound();
		compound.setTag(key, tag);
		tag.setDouble("hunger", this.hunger);
		tag.setDouble("excretion", this.excretion);
		tag.setDouble("tamedValue", this.taming);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		NBTTagCompound tag = (NBTTagCompound) compound.getTag(key);
		if (tag != null) {
			this.hunger = tag.getDouble("hunger");
			this.excretion = tag.getDouble("excretion");
			this.taming = tag.getDouble("tamedValue");
		}
	}

	@Override
	public void init(Entity entity, World world) {
		this.entity = (EntityAnimal) entity;
		this.worldObj = world;
		this.ai_moveToFoodbox = new EntityAIMoveToTrough(this.entity, this, 1.0D);
	}

	public void postInit() {
		this.removeAI(new Class[] { EntityAITempt.class, EntityAIFollowParent.class, EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class, EntityAIWatchClosest.class, EntityAILookIdle.class });

		//this.entity.tasks.addTask(0, this.ai_crank);
		this.entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, this, 16.0F, 1.0D, 2.0D));
		this.entity.tasks.addTask(2, new EntityAIMateModified(this.entity, this, 2.0D));
		this.entity.tasks.addTask(3, this.ai_moveToFoodbox);
		this.entity.tasks.addTask(4, new EntityAITemptEdibleItem(this.entity, this, 1.5D));
		this.entity.tasks.addTask(5, new EntityAIMoveToEatItem(this.entity, this, 1.5D));
		this.entity.tasks.addTask(7, new EntityAIMoveToEatBlock(this.entity, this, 1.0D));
		this.entity.tasks.addTask(8, new EntityAIWander(this.entity, 1.0D));
		this.entity.tasks.addTask(9, new EntityAIWatchClosest(this.entity, EntityPlayer.class, 6.0F));
		this.entity.tasks.addTask(10, new EntityAILookIdle(this.entity));
		
		HungryAnimalManager.getInstance().registerAttributes(this.entity);
		acceptProperty();
		
		taming_factor = 0.998;
		this.hunger = this.entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue()/2.0;
		this.excretion = 0;
		this.taming = -2;
	}

	public void dropFewItems(boolean isHitByPlayer, int looting, List<EntityItem> drops) {
		if (this.entity.getGrowingAge() >= 0) {
			ArrayList<EntityItem> toRemove = new ArrayList<EntityItem>();
			for (EntityItem i : drops) {
				for (ValueDropMeat j : drop_meat) {
					if (i.getEntityItem().isItemEqual(j.getItemStack())) {
						toRemove.add(i);
					}
				}
				for (ValueDropRandom j : drop_random) {
					if (i.getEntityItem().isItemEqual(j.getItemStack())) {
						toRemove.add(i);
					}
				}
				for (ValueDropRare j : drop_rare) {
					if (i.getEntityItem().isItemEqual(j.getItemStack())) {
						toRemove.add(i);
					}
				}
			}
			for (EntityItem i : toRemove) {
				drops.remove(i);
			}

			for (ValueDropMeat j : drop_meat) {
				ItemStack drop = j.getDrop(getHungry());
				if (drop != null) {
					drop.stackSize = (int) (drop.stackSize * (1 + entity.getRNG().nextInt(1 + looting) / 3.0));
					EntityItem entityitem = new EntityItem(this.worldObj, this.entity.posX, this.entity.posY, this.entity.posZ, drop);
					entityitem.setDefaultPickupDelay();
					drops.add(entityitem);
				}
			}
			for (ValueDropRandom j : drop_random) {
				ItemStack drop = j.getDrop(entity.getRNG());
				if (drop != null) {
					drop.stackSize = (int) (drop.stackSize * (1 + (entity.getRNG().nextInt(1 + looting)) / 3.0));
					EntityItem entityitem = new EntityItem(this.worldObj, this.entity.posX, this.entity.posY, this.entity.posZ, drop);
					entityitem.setDefaultPickupDelay();
					drops.add(entityitem);
				}
			}
			for (ValueDropRare j : drop_rare) {
				ItemStack drop = j.getDrop(entity.getRNG(), looting);
				if (drop != null) {
					EntityItem entityitem = new EntityItem(this.worldObj, this.entity.posX, this.entity.posY, this.entity.posZ, drop);
					entityitem.setDefaultPickupDelay();
					drops.add(entityitem);
				}
			}
		}
	}

	public double getHungry() {
		return this.hunger/entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue();
	}

	protected void removeAI(Class[] target) {
		List removeEntries = new ArrayList();
		for (Object i : this.entity.tasks.taskEntries) {
			for (Class j : target) {
				if (((EntityAITaskEntry) i).action.getClass() == j) {
					removeEntries.add(((EntityAITaskEntry) i).action);
				}
			}
		}
		for (Object i : removeEntries) {
			this.entity.tasks.removeTask(((EntityAIBase) i));
		}
	}

	public double getFoodHunger(ItemStack food) {
		HashItemType key;
		if (this.hunger_food.containsKey(key = new HashItemType(food.getItem()))) {
			return this.hunger_food.get(key);
		} else if (this.hunger_food.containsKey(key = new HashItemType(food.getItem(), food.getItemDamage()))) {
			return this.hunger_food.get(key);
		} else {
			return 0;
		}
	}

	public double getBlockHunger(IBlockState block) {
		HashBlockState key;
		if (this.hunger_block.containsKey(key = new HashBlockState(block, true))) {
			return this.hunger_block.get(key);
		} else if (this.hunger_block.containsKey(key = new HashBlockState(block, false))) {
			return this.hunger_block.get(key);
		} else {
			return 0;
		}
	}

	public boolean canEatFood(ItemStack food) {

		double saturation = getFoodHunger(food);

		if (saturation == 0) {
			return false;
		} else {
			return saturation + this.hunger < entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue();
		}

	}

	public boolean canEatBlock(IBlockState block) {
		double saturation = getBlockHunger(block);

		if (saturation == 0) {
			return false;
		} else {
			return saturation + this.hunger < entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue();
		}
	}

	public void addHunger(double value) {
		this.hunger += value;
		if (this.hunger > this.entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue()) {
			this.hunger = this.entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue();
		}
	}

	public void subHunger(double value) {
		this.hunger -= value;
		if (hunger <= 0) {
			this.hunger = 0;
		}
		this.excretion += value * entity.getAttributeMap().getAttributeInstance(ModAttributes.excretion_factor).getAttributeValue();
	}

	public void eatFoodBonus(ItemStack item) {
		if (item == null)
			return;

		double hunger = getFoodHunger(item);
		this.addHunger(hunger);

		if (this.entity.getGrowingAge() < 0) {
			NBTTagCompound tag = item.getTagCompound();
			if (tag == null || !tag.hasKey("isNatural") || !tag.getBoolean("isNatural")) {
				int duration = (int) (hunger / entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_bmr).getAttributeValue());
				this.entity.addPotionEffect(new PotionEffect(ModPotions.potionGrowth.id, duration, 1));
			}
		}

		NBTTagCompound tag = item.getTagCompound();
		if (tag == null || !tag.hasKey("isNatural") || !tag.getBoolean("isNatural")) {
			this.taming += 0.0002 / entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_bmr).getAttributeValue() * hunger;
		}

	}

	public void eatBlockBonus(IBlockState block) {
		if (block == null)
			return;

		double hunger = getBlockHunger(block);
		this.addHunger(hunger);
	}

	public void update() {
		if (!this.worldObj.isRemote) {

			updateHunger();
			updateCourtship();
			updateExcretion();
			updateTaming();
			updateEnvironmentalEffet();
			updateRecovery();

			if (this.getHungry() == 0) {
				onStarve();
			}
		}
	}

	private void updateHunger() {
		/*
		 * double vel = (!this.entity.isAirBorne) ? this.entity.motionX
		 * this.entity.motionX + this.entity.motionY this.entity.motionY +
		 * this.entity.motionZ this.entity.motionZ : 0; vel = 20 *
		 * Math.sqrt(vel); this.subHunger(this.hunger_bmr * (1 + vel / 2.0));
		 */
		this.subHunger(entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_bmr).getAttributeValue());
	}

	private void updateCourtship() {
		if (this.entity.getGrowingAge() == 0 && !this.entity.isInLove() && this.getHungry() > entity.getAttributeMap().getAttributeInstance(ModAttributes.courtship_hungerCondition).getAttributeValue() && this.entity.getRNG().nextDouble() < entity.getAttributeMap().getAttributeInstance(ModAttributes.courtship_probability).getAttributeValue()) {
			this.entity.setInLove(null);
			this.subHunger(entity.getAttributeMap().getAttributeInstance(ModAttributes.courtship_hunger).getAttributeValue());
		}
	}

	private void updateExcretion() {
		if (this.excretion > 1) {
			this.excretion -= 1;
			int x = (int) this.entity.posX;
			int y = (int) this.entity.posY;
			int z = (int) this.entity.posZ;

			BlockPos pos = new BlockPos(x, y, z);
			IBlockState meta = this.worldObj.getBlockState(pos);
			Block block = meta.getBlock();

			if (block == ModBlocks.excreta) {
				int exc = ((BlockExcreta.EnumType) meta.getValue(BlockExcreta.CONTENT)).getExcreta();
				int man = ((BlockExcreta.EnumType) meta.getValue(BlockExcreta.CONTENT)).getManure();
				if (exc + man < 4) {
					this.worldObj.setBlockState(pos, meta.withProperty(BlockExcreta.CONTENT, BlockExcreta.EnumType.getValue(exc + 1, man)), 2);
				} else if (exc + man == 4) {

				}
			} else if (block.isAir(this.worldObj, pos) || block.isReplaceable(this.worldObj, pos)) {
				this.worldObj.setBlockState(pos, ModBlocks.excreta.getDefaultState().withProperty(BlockExcreta.CONTENT, BlockExcreta.EnumType.getValue(1, 0)), 2);
			}
		}
	}

	private void updateEnvironmentalEffet() {
		IBlockState floor = worldObj.getBlockState(entity.getPosition().down());
		if (floor.getBlock() == ModBlocks.floorcover_leaf) {
			int j = this.entity.getGrowingAge();
			if (j < 0) {
				j += (int) (this.entity.getRNG().nextInt(4) / 4.0);
				this.entity.setGrowingAge(j);
			}
		}
		if (floor.getBlock() == ModBlocks.floorcover_wool) {
			int j = this.entity.getGrowingAge();
			if (j > 0) {
				j -= (int) (this.entity.getRNG().nextInt(4) / 4.0);
				this.entity.setGrowingAge(j);
			}
		}
		if (floor.getBlock() == ModBlocks.floorcover_ironbar) {
			//TODO
		}
	}

	private void updateTaming() {
		double radius = 16;

		if ((this.worldObj.getWorldTime() + this.entity.getEntityId()) % 100 == 0) {
			ArrayList<EntityPlayer> players = (ArrayList<EntityPlayer>) this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, entity.getEntityBoundingBox().expand(radius, radius, radius));
			if (players.isEmpty()) {
				if (this.taming > 0)
					this.taming *= this.taming_factor;
			} else {
				if (this.taming < 0)
					this.taming *= this.taming_factor;
			}
		}
	}

	private void updateRecovery() {
		if (this.entity.getHealth() < this.entity.getMaxHealth() && this.getHungry() > 0.8 && (this.worldObj.getWorldTime() % 200) == 0) {
			this.entity.heal(1.0F);
			this.subHunger(entity.getAttributeMap().getAttributeInstance(ModAttributes.hunger_max).getAttributeValue() / this.entity.getMaxHealth());
		}
	}

	public void onStarve() {
		this.entity.attackEntityFrom(DamageSource.starve, 0.5F);
	}

	public void onAttackedByPlayer(float damage, DamageSource source) {
		if (!this.entity.isEntityInvulnerable(source)) {
			if (source.getSourceOfDamage() instanceof EntityPlayer) {
				this.taming -= 4 / this.entity.getEntityAttribute(SharedMonsterAttributes.maxHealth).getBaseValue() * damage;
			}
		}
	}

	public boolean interact(EntityPlayer entity) {

		ItemStack stack = entity.getCurrentEquippedItem();
		if (stack == null)
			return false;

		if (this.canEatFood(stack) && this.taming >= 1) {
			this.eatFoodBonus(stack);
			stack.stackSize--;
			if (stack.stackSize == 0) {
				entity.inventory.setInventorySlotContents(entity.inventory.currentItem, null);
			}
			return true;
		} else if (this.entity.isPotionActive(ModPotions.potionDisease) && this.taming >= 1) {
			if (stack.getItem() == ItemBlock.getItemFromBlock(Blocks.red_mushroom) || stack.getItem() == ItemBlock.getItemFromBlock(Blocks.brown_mushroom)) {
				this.entity.removePotionEffect(ModPotions.potionDisease.id);
				stack.stackSize--;
				if (stack.stackSize == 0) {
					entity.inventory.setInventorySlotContents(entity.inventory.currentItem, null);
				}
			}
			return true;
		}

		if (this.entity.isBreedingItem(stack)) {
			return true;
		}
		// To avoid vanilla breeding.

		return false;
	}

	public double getBlockPathWeight(BlockPos pos) {
		IBlockState state = this.worldObj.getBlockState(pos);
		if (state.getBlock()==ModBlocks.excreta) {
			return -1.0;
		} else if (canEatBlock(state)) {
			return getBlockHunger(state);
		} else {
			return 1.0;
		}
	}

}
