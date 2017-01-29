package oortcloud.hungryanimals.entities.properties;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import oortcloud.hungryanimals.entities.properties.handler.HungryAnimalManager;
import oortcloud.hungryanimals.entities.attributes.ModAttributes;
import oortcloud.hungryanimals.entities.properties.handler.AnimalCharacteristic;

public class ExtendedPropertiesHungrySheep extends ExtendedPropertiesHungryAnimal {

	public EntitySheep entity;
	public int wool;

	@Override
	public void init(Entity entity, World world) {
		super.init(entity, world);
		this.entity = (EntitySheep) entity;
	}
	
	@Override
	public void acceptProperty() {
		super.acceptProperty();
		this.wool = (int) this.entity.getAttributeMap().getAttributeInstance(ModAttributes.wool_delay).getAttributeValue();
	}
	
	@Override
	public void update() {
		if (!this.worldObj.isRemote) {
			if (this.wool > 0 && this.entity.getSheared()) {
				this.wool--;
			}
			if (this.wool == 0 && this.entity.getSheared()) {
				this.entity.setSheared(false);
				this.subHunger(entity.getAttributeMap().getAttributeInstance(ModAttributes.wool_hunger).getAttributeValue());
				this.wool = (int)entity.getAttributeMap().getAttributeInstance(ModAttributes.wool_delay).getAttributeValue();
			}
		}
		super.update();
	}

	@Override
	protected void loadPropertyNBTData(NBTTagCompound tag) {
		super.loadPropertyNBTData(tag);
		wool = tag.getInteger("wool");
	}

	@Override
	protected void savePropertyNBTData(NBTTagCompound tag) {
		super.savePropertyNBTData(tag);
		tag.setInteger("wool", wool);
	}

}
