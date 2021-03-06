package oortcloud.hungryanimals.api;

import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.biome.Biome;
import oortcloud.hungryanimals.entities.ai.handler.AIContainer;
import oortcloud.hungryanimals.entities.ai.handler.AIContainers;
import oortcloud.hungryanimals.entities.ai.handler.IAIContainer;
import oortcloud.hungryanimals.entities.attributes.ModAttributes;
import oortcloud.hungryanimals.entities.handler.Cures;
import oortcloud.hungryanimals.entities.handler.HungryAnimalManager;
import oortcloud.hungryanimals.entities.handler.InHeats;
import oortcloud.hungryanimals.generation.GrassGenerator;
import oortcloud.hungryanimals.generation.GrassGenerators;

public class API {
	
	/**
	 * It registers given animalclass to Hungry Animals.
	 * This will make the animals have default ExtendedProperty.
	 * It also enables other registration for attributes and AIs.
	 * 
	 * @param animalclass
	 * @return true if registration failed, otherwise false
	 */
	public static boolean registerAnimal(Class<? extends EntityAnimal> animalclass) {
		return HungryAnimalManager.getInstance().register(animalclass);
	}
	
	/**
	 * It registers the attribute with val to the animalclass.
	 * When shouldRegistered is true, the attribute will be registered to the entity,
	 * when shouldRegistered is false, the attribute will only be set the base value as val.
	 * You must set shouldRegistered to false for attributes that is registered by vanila or other mods.
	 * For example, max health and move speed.
	 * This val may be ignored and a value from configuration could be used.
	 * 
	 * @param animalclass
	 * @param attribute
	 * @param val
	 * @param shouldRegistered
	 * @return true if registration failed, otherwise false
	 */
	public static boolean registerAttribute(Class<? extends EntityAnimal> animalclass, IAttribute attribute, double val, boolean shouldRegistered) {
		return ModAttributes.getInstance().register(animalclass, attribute, val, shouldRegistered);
	}
	
	public static boolean registerCure(Ingredient cure) {
		return Cures.getInstance().register(cure);
	}
	
	public static boolean registerInHeat(Ingredient cure, int duration) {
		return InHeats.getInstance().register(cure, duration);
	}
	
	public static boolean registerGrassGenerator(Biome biome, GrassGenerator generator) {
		return GrassGenerators.getInstance().register(biome, generator);
	}
	
	/**
	 * It registers given aifactory to the animalclass.
	 * AIFactory is a functional interface, which takes EntityAnimal and returns EntityAIBase.
	 * 
	 * @param animalclass
	 * @param ai
	 * @return true if registration failed, otherwise false
	 */
	public static IAIContainer<EntityAnimal> registerAIContainer(Class<? extends EntityAnimal> animalclass, AIContainer aiContainer) {
		return AIContainers.getInstance().register(animalclass, aiContainer);
	}
	
}
