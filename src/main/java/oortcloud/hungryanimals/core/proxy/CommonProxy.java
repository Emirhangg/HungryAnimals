package oortcloud.hungryanimals.core.proxy;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.api.theoneprobe.TOPCompatibility;
import oortcloud.hungryanimals.configuration.ConfigurationEventHandler;
import oortcloud.hungryanimals.core.handler.WorldEventHandler;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.core.lib.Strings;
import oortcloud.hungryanimals.entities.EntityBola;
import oortcloud.hungryanimals.entities.EntitySlingShotBall;
import oortcloud.hungryanimals.entities.capability.CapabilityHungryAnimal;
import oortcloud.hungryanimals.entities.capability.CapabilityTamableAnimal;
import oortcloud.hungryanimals.entities.capability.ICapabilityHungryAnimal;
import oortcloud.hungryanimals.entities.capability.ICapabilityTamableAnimal;
import oortcloud.hungryanimals.entities.capability.StorageHungryAnimal;
import oortcloud.hungryanimals.entities.capability.StorageTamableAnimal;
import oortcloud.hungryanimals.entities.event.EntityEventHandler;
import oortcloud.hungryanimals.entities.loot_tables.LootTableModifier;
import oortcloud.hungryanimals.recipes.event.CraftingEventHandler;
import oortcloud.hungryanimals.tileentities.TileEntityTrough;

public class CommonProxy {

	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityTrough.class, References.MODID+"."+Strings.blockTroughName);
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(new ResourceLocation(References.MODID, Strings.entityBolaName), EntityBola.class, Strings.entityBolaName, Strings.entityBolaID, HungryAnimals.instance, 80, 3, true);
		EntityRegistry.registerModEntity(new ResourceLocation(References.MODID, Strings.entitySlingShotBallName), EntitySlingShotBall.class, Strings.entitySlingShotBallName, Strings.entitySlingShotBallID, HungryAnimals.instance, 80,
				3, true);
	}

	public void registerCapabilities() {
		CapabilityManager.INSTANCE.register(ICapabilityHungryAnimal.class, new StorageHungryAnimal(), CapabilityHungryAnimal.class);
		CapabilityManager.INSTANCE.register(ICapabilityTamableAnimal.class, new StorageTamableAnimal(), CapabilityTamableAnimal.class);
	}

	public void registerColors() {
	}
	
	public void registerEntityRendering() {
	}

	public void registerTileEntityRendering() {
	}

	public void registerEventHandler() {
		MinecraftForge.EVENT_BUS.register(new WorldEventHandler());
		MinecraftForge.EVENT_BUS.register(new ConfigurationEventHandler());
		MinecraftForge.EVENT_BUS.register(new EntityEventHandler());
		MinecraftForge.EVENT_BUS.register(new CraftingEventHandler());
		MinecraftForge.EVENT_BUS.register(new LootTableModifier());
	}

	public void registerKeyBindings() {
	}

	public void initNEI() {
	}

	public void initWAILA() {
		// FMLInterModComms.sendMessage("Waila", "register",
		// "oortcloud.hungryanimals.api.waila.HUDHandlerHungryAnimals.callbackRegister");
	}
	
	public void initTOP() {
		 TOPCompatibility.register();
	}

}
