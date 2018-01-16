package oortcloud.hungryanimals.entities.capability;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.entities.handler.HungryAnimalManager;
import oortcloud.hungryanimals.entities.production.Productions;

@Mod.EventBusSubscriber
public class CapabilityHandler {
	public static final ResourceLocation CAP_HUNGRYANIMALS = new ResourceLocation(References.MODID, "hungryanimal");
	public static final ResourceLocation CAP_TAMABLEANIMALS = new ResourceLocation(References.MODID, "tamableanimal");
	public static final ResourceLocation CAP_PRODUCINGANIMALS = new ResourceLocation(References.MODID, "producinganimal");
	
	@SubscribeEvent
	public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (!(event.getObject() instanceof EntityAnimal))
			return;

		EntityAnimal animal = (EntityAnimal) event.getObject();
		if (HungryAnimalManager.getInstance().isRegistered(animal.getClass())) {
			event.addCapability(CAP_HUNGRYANIMALS, new ProviderHungryAnimal(animal));

			boolean disabledTaming = HungryAnimalManager.getInstance().isDisabledTaming(animal.getClass());

			if (!disabledTaming) {
				if (animal instanceof AbstractHorse) {
					event.addCapability(CAP_TAMABLEANIMALS, new ProviderTamableAnimal((AbstractHorse) animal));
				} else {
					event.addCapability(CAP_TAMABLEANIMALS, new ProviderTamableAnimal(animal));
				}
			}
			if (Productions.getInstance().hasProduction(animal)) {
				event.addCapability(CAP_PRODUCINGANIMALS, new ProviderProducingAnimal(animal));
			}
		}
	}

	@SubscribeEvent
	public static void onStartTracking(PlayerEvent.StartTracking event) {
		Entity target = event.getTarget();
		CapabilityTamableAnimal capTamable = (CapabilityTamableAnimal) target.getCapability(ProviderTamableAnimal.CAP, null);
		if (capTamable != null)
			capTamable.syncTo((EntityPlayerMP) event.getEntityPlayer());
		CapabilityHungryAnimal capHungry = (CapabilityHungryAnimal) target.getCapability(ProviderHungryAnimal.CAP, null);
		if (capHungry != null)
		capHungry.syncTo((EntityPlayerMP) event.getEntityPlayer());
	}

}
