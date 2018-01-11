package oortcloud.hungryanimals.entities.ai.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.init.Items;
import net.minecraft.util.JsonUtils;
import oortcloud.hungryanimals.HungryAnimals;
import oortcloud.hungryanimals.entities.ai.EntityAIMateModified;
import oortcloud.hungryanimals.entities.ai.EntityAIMoveToEatBlock;
import oortcloud.hungryanimals.entities.ai.EntityAIMoveToEatItem;
import oortcloud.hungryanimals.entities.ai.EntityAIMoveToTrough;
import oortcloud.hungryanimals.entities.ai.EntityAIHunt;
import oortcloud.hungryanimals.entities.ai.EntityAITemptEdibleItem;
import oortcloud.hungryanimals.entities.ai.handler.AIContainerTask.AIRemoverIsInstance;

public class AIContainers {

	private static AIContainers INSTANCE;

	public Map<Class<? extends EntityAnimal>, IAIContainer<EntityAnimal>> REGISTRY;
	private Map<String, Function<JsonElement, IAIContainer<EntityAnimal>>> PARSERS;
	
	private AIContainers() {
		REGISTRY = new HashMap<Class<? extends EntityAnimal>, IAIContainer<EntityAnimal>>();
		PARSERS = new HashMap<String, Function<JsonElement, IAIContainer<EntityAnimal>>>();
	}

	public static AIContainers getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AIContainers();
		}
		return INSTANCE;
	}

	public IAIContainer<EntityAnimal> register(Class<? extends EntityAnimal> animal, IAIContainer<EntityAnimal> aiContainer) {
		return REGISTRY.put(animal, aiContainer);
	}
	
	public void init() {
		PARSERS.put("herbivore", AIContainerHerbivore::parse);
		
		PARSERS.put("rabbit", (jsonEle) -> {
			AIContainer aiContainer = (AIContainer) AIContainerHerbivore.parse(jsonEle);
			aiContainer.getTask().remove(new AIRemoverIsInstance(EntityAIPanic.class));
			aiContainer.getTask().remove(new AIRemoverIsInstance(EntityAIAvoidEntity.class));
			aiContainer.getTask().remove(new AIRemoverIsInstance(EntityAIMoveToBlock.class));
			return aiContainer;
		});
		
		PARSERS.put("pig", (jsonEle) -> {
			AIContainer aiContainer = (AIContainer) AIContainerHerbivore.parse(jsonEle);
			aiContainer.getTask().before(EntityAITemptEdibleItem.class)
					.put((entity) -> new EntityAITempt(entity, 1.5D, Items.CARROT_ON_A_STICK, false));
			return aiContainer;
		});
		
		PARSERS.put("wolf", AIContainerWolf::parse);

		PARSERS.put("polar_bear", (jsonEle)->{
			AIContainer aiContainer = new AIContainer();
			aiContainer.getTask().before(EntityAIFollowParent.class).put((entity) -> new EntityAIMateModified(entity, 2.0D));
			aiContainer.getTask().before(EntityAIFollowParent.class).put((entity) -> new EntityAIMoveToTrough(entity, 1.0D));
			aiContainer.getTask().before(EntityAIFollowParent.class).put((entity) -> new EntityAITemptEdibleItem(entity, 1.5D, false));
			aiContainer.getTask().before(EntityAIFollowParent.class).put((entity) -> new EntityAIMoveToEatItem(entity, 1.5D));
			aiContainer.getTask().before(EntityAIFollowParent.class).put((entity) -> new EntityAIMoveToEatBlock(entity, 1.0D));
			
			aiContainer.getTarget().putLast((entity) -> new EntityAIHunt(entity, 1, false, false, false));
			
			return aiContainer;
	    });
	}
	
	public IAIContainer<EntityAnimal> parse(JsonElement jsonEle) {
		if (! (jsonEle instanceof JsonObject)) {
			HungryAnimals.logger.error("AI container must an object.");
			throw new JsonSyntaxException(jsonEle.toString());
		}
		JsonObject jsonObj = (JsonObject) jsonEle;

		String aiType = JsonUtils.getString(jsonObj, "type");
		
		return PARSERS.get(aiType).apply(jsonObj);
	}

}
