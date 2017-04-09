package oortcloud.hungryanimals.items;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.core.lib.Strings;
import oortcloud.hungryanimals.core.network.PacketPlayerServer;
import oortcloud.hungryanimals.core.network.SyncIndex;
import oortcloud.hungryanimals.entities.capability.ICapabilityHungryAnimal;
import oortcloud.hungryanimals.entities.capability.ICapabilityTamableAnimal;
import oortcloud.hungryanimals.entities.capability.ProviderHungryAnimal;
import oortcloud.hungryanimals.entities.capability.ProviderTamableAnimal;
import oortcloud.hungryanimals.entities.properties.handler.HungryAnimalManager;

public class ItemDebugGlass extends Item {

	public ItemDebugGlass() {
		super();
		setUnlocalizedName(References.MODID + "." + Strings.itemDebugGlassName);
		setRegistryName(Strings.itemDebugGlassName);
		setCreativeTab(HungryAnimals.tabHungryAnimals);

		setMaxStackSize(1);
		GameRegistry.register(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if (worldIn.isRemote) {
			Entity entity = Minecraft.getMinecraft().objectMouseOver.entityHit;
			if (entity != null) {
				PacketPlayerServer msg = new PacketPlayerServer(SyncIndex.DEBUG_SETTARGET, playerIn.getName());
				msg.setInt(entity.getEntityId());
				HungryAnimals.simpleChannel.sendToServer(msg);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStackIn);
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!worldIn.isRemote) {
			NBTTagCompound tag = stack.getTagCompound();
			if (tag != null && tag.hasKey("target")) {
				Entity target = worldIn.getEntityByID(tag.getInteger("target"));
				if (target != null) {
					if (!(target instanceof EntityAnimal))
						return;

					EntityAnimal entity = (EntityAnimal) target;
					if (!HungryAnimalManager.getInstance().isRegistered(entity.getClass()))
						return;

					ICapabilityHungryAnimal capHungry = entity.getCapability(ProviderHungryAnimal.CAP, null);
					ICapabilityTamableAnimal capTaming = entity.getCapability(ProviderTamableAnimal.CAP, null);

					tag.setDouble("hunger", capHungry.getHunger());
					tag.setDouble("excretion", capHungry.getExcretion());
					tag.setDouble("taming", capTaming.getTaming());
					tag.setInteger("age", ((EntityAnimal) target).getGrowingAge());
				}
			}
		}
	}
}
