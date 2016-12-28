package oortcloud.hungryanimals.entities.event;

import net.minecraft.entity.passive.EntityAnimal;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import oortcloud.hungryanimals.core.lib.Strings;
import oortcloud.hungryanimals.entities.properties.ExtendedPropertiesHungryAnimal;
import oortcloud.hungryanimals.entities.properties.handler.HungryAnimalManager;

public class EntityEventHandler {

	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		if (!(event.getEntity() instanceof EntityAnimal))
			return;
		
		EntityAnimal animal = (EntityAnimal) event.getEntity();
		
		if (HungryAnimalManager.getInstance().isRegistered(animal.getClass())) {
			event.getEntity().registerExtendedProperties(Strings.extendedPropertiesKey, HungryAnimalManager.getInstance().createProperty(animal));
		}
		
	}

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		IExtendedEntityProperties property = event.getEntity().getExtendedProperties(Strings.extendedPropertiesKey);
		if (property != null) {
			((ExtendedPropertiesHungryAnimal)property).postInit();
		}
	}

	@SubscribeEvent
	public void onLivingEntityUpdate(LivingEvent.LivingUpdateEvent event) {
		IExtendedEntityProperties property = event.getEntity().getExtendedProperties(Strings.extendedPropertiesKey);
		if (property != null) {
			((ExtendedPropertiesHungryAnimal) property).update();
		}
	}

	@SubscribeEvent
	public void onLivingEntityAttackedByPlayer(LivingAttackEvent event) {
		IExtendedEntityProperties property = event.getEntity().getExtendedProperties(Strings.extendedPropertiesKey);
		if (property != null) {
			((ExtendedPropertiesHungryAnimal) property).onAttackedByPlayer(event.getAmount(), event.getSource());
		}
	}

	@SubscribeEvent
	public void onInteract(EntityInteract event) {
		IExtendedEntityProperties property = event.getEntity().getExtendedProperties(Strings.extendedPropertiesKey);
		if (property != null) {
			event.setCanceled(((ExtendedPropertiesHungryAnimal)property).interact(event.getEntityPlayer()));
		}
	}
	
	@SubscribeEvent
	public void onEntityDrops(LivingDropsEvent event) {
		IExtendedEntityProperties property = event.entity.getExtendedProperties(Strings.extendedPropertiesKey);
		if (property != null) {
			((ExtendedPropertiesHungryAnimal)property).dropFewItems(event.isRecentlyHit(), event.getLootingLevel(), event.getDrops());
		}
	}

}
