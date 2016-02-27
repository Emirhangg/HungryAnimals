package oortcloud.hungryanimals.items;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.*;

import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;

import java.util.List;
import java.lang.reflect.Field;

import javax.vecmath.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IFlexibleBakedModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.core.lib.Strings;
import oortcloud.hungryanimals.entities.EntityBola;
import oortcloud.hungryanimals.items.render.CameraTransformModelItemBola;
import scala.reflect.api.Trees.IfExtractor;
import net.minecraftforge.client.model.*;

public class ItemBola extends Item {
	
	public ItemBola() {
		super();
		setUnlocalizedName(Strings.itemBolaName);
		setCreativeTab(HungryAnimals.tabHungryAnimals);

		ModItems.register(this);
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int tick) {

		int duration = this.getMaxItemUseDuration(stack) - tick;
		ArrowLooseEvent event = new ArrowLooseEvent(player, stack, duration);
		MinecraftForge.EVENT_BUS.post(event);
		if (event.isCanceled()) {
			return;
		}
		if (duration >= 100)
			duration = 100;

		if (!world.isRemote) {
			if (!player.capabilities.isCreativeMode) {
				player.getCurrentEquippedItem().stackSize--;
				if (player.getCurrentEquippedItem().stackSize == 0)
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			}
			EntityBola bola = new EntityBola(world, player, (float) (0.015 * duration));
			world.spawnEntityInWorld(bola);
		}
	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {

		player.setItemInUse(item, this.getMaxItemUseDuration(item));

		return item;
	}

}
