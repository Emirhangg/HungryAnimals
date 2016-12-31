package oortcloud.hungryanimals.entities.properties;

import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import oortcloud.hungryanimals.entities.properties.handler.HungryAnimalManager;
import oortcloud.hungryanimals.entities.properties.handler.AnimalCharacteristic;
import oortcloud.hungryanimals.entities.properties.handler.ModAttributes;

public class ExtendedPropertiesHungryChicken extends ExtendedPropertiesHungryAnimal {

	public EntityChicken entity;

	@Override
	public void init(Entity entity, World world) {
		super.init(entity, world);
		this.entity = (EntityChicken) entity;
	}

	@Override
	public void eatBlockBonus(IBlockState block) {
		if (block.getBlock() == Blocks.TALLGRASS) {
			double prob = getBlockHunger(block) / 2.0 / getFoodHunger(new ItemStack(Items.WHEAT_SEEDS));
			if (this.entity.getRNG().nextDouble() < prob) {
				ItemStack stack = new ItemStack(Items.WHEAT_SEEDS);
				NBTTagCompound tag = new NBTTagCompound();
				tag.setBoolean("isNatural", true);
				stack.setTagCompound(tag);
				EntityItem entity = new EntityItem(worldObj, this.entity.posX, this.entity.posY, this.entity.posZ, stack);
				entity.motionY = 0.2;
				worldObj.spawnEntityInWorld(entity);
			}
		} else {
			super.eatBlockBonus(block);
		}
	}

}
