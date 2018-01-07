package oortcloud.hungryanimals.entities.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.passive.EntityAnimal;

public class HungryAnimalManager {

	private static HungryAnimalManager INSTANCE;

	private List<Class<? extends EntityAnimal>> registedClass;

	public static HungryAnimalManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new HungryAnimalManager();
		}
		return INSTANCE;
	}

	private HungryAnimalManager() {
		registedClass = new ArrayList<Class<? extends EntityAnimal>>();
	}

	public void registerHungryAnimal(Class<? extends EntityAnimal> animal) {
		if (!registedClass.contains(animal)) {
			registedClass.add(animal);
		}
	}

	public List<Class<? extends EntityAnimal>> getRegisteredAnimal() {
		return registedClass;
	}

	public boolean isRegistered(Class<? extends EntityAnimal> animal) {
		return registedClass.contains(animal);
	}

	public void init() {

	}

}
