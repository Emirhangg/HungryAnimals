package oortcloud.hungryanimals.entities.ai;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;

public class AIManager {

	private static AIManager INSTANCE;

	public Map<Class<? extends EntityAnimal>, IAIContainer<EntityAnimal>> REGISTRY;

	private AIManager() {
		REGISTRY = new HashMap<Class<? extends EntityAnimal>, IAIContainer<EntityAnimal>>();
	}
	
	public static AIManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AIManager();
		}
		return INSTANCE;
	}

	public void init() {
		REGISTRY.put(EntityChicken.class, new IAIContainer<EntityAnimal>() {

			@Override
			public void registerAI(EntityAnimal entity) {
				removeAI(entity,
						Arrays.asList(new Class[] { EntityAITempt.class, EntityAIFollowParent.class,
								EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class,
								EntityAIWatchClosest.class, EntityAILookIdle.class }));

				// this.entity.tasks.addTask(0, this.ai_crank);
				entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, 16.0F, 1.0D, 2.0D));
				entity.tasks.addTask(2, new EntityAIMateModified(entity, 2.0D));
				entity.tasks.addTask(3, new EntityAIMoveToTrough(entity, 1.0D));
				entity.tasks.addTask(4, new EntityAITemptEdibleItem(entity, 1.5D, false));
				entity.tasks.addTask(5, new EntityAIMoveToEatItem(entity, 1.5D));
				entity.tasks.addTask(7, new EntityAIMoveToEatBlockChicken(entity, 1.0D));
				entity.tasks.addTask(8, new EntityAIWander(entity, 1.0D));
				entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
				entity.tasks.addTask(10, new EntityAILookIdle(entity));
			}
		});

		REGISTRY.put(EntityCow.class, new IAIContainer<EntityAnimal>() {

			@Override
			public void registerAI(EntityAnimal entity) {
				removeAI(entity,
						Arrays.asList(new Class[] { EntityAITempt.class, EntityAIFollowParent.class,
								EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class,
								EntityAIWatchClosest.class, EntityAILookIdle.class }));

				// this.entity.tasks.addTask(0, this.ai_crank);
				entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, 16.0F, 1.0D, 2.0D));
				entity.tasks.addTask(2, new EntityAIMateModified(entity, 2.0D));
				entity.tasks.addTask(3, new EntityAIMoveToTrough(entity, 1.0D));
				entity.tasks.addTask(4, new EntityAITemptEdibleItem(entity, 1.5D, false));
				entity.tasks.addTask(5, new EntityAIMoveToEatItem(entity, 1.5D));
				entity.tasks.addTask(7, new EntityAIMoveToEatBlock(entity, 1.0D));
				entity.tasks.addTask(8, new EntityAIWander(entity, 1.0D));
				entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
				entity.tasks.addTask(10, new EntityAILookIdle(entity));
			}
		});
		
		REGISTRY.put(EntityMooshroom.class, new IAIContainer<EntityAnimal>() {

			@Override
			public void registerAI(EntityAnimal entity) {
				removeAI(entity,
						Arrays.asList(new Class[] { EntityAITempt.class, EntityAIFollowParent.class,
								EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class,
								EntityAIWatchClosest.class, EntityAILookIdle.class }));

				// this.entity.tasks.addTask(0, this.ai_crank);
				entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, 16.0F, 1.0D, 2.0D));
				entity.tasks.addTask(2, new EntityAIMateModified(entity, 2.0D));
				entity.tasks.addTask(3, new EntityAIMoveToTrough(entity, 1.0D));
				entity.tasks.addTask(4, new EntityAITemptEdibleItem(entity, 1.5D, false));
				entity.tasks.addTask(5, new EntityAIMoveToEatItem(entity, 1.5D));
				entity.tasks.addTask(7, new EntityAIMoveToEatBlock(entity, 1.0D));
				entity.tasks.addTask(8, new EntityAIWander(entity, 1.0D));
				entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
				entity.tasks.addTask(10, new EntityAILookIdle(entity));
			}
		});
		
		REGISTRY.put(EntityPig.class, new IAIContainer<EntityAnimal>() {

			@Override
			public void registerAI(EntityAnimal entity) {
				removeAI(entity,
						Arrays.asList(new Class[] { EntityAITempt.class, EntityAIFollowParent.class,
								EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class,
								EntityAIWatchClosest.class, EntityAILookIdle.class }));

				// this.entity.tasks.addTask(0, this.ai_crank);
				entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, 16.0F, 1.0D, 2.0D));
				entity.tasks.addTask(2, new EntityAIMateModified(entity, 2.0D));
				entity.tasks.addTask(3, new EntityAIMoveToTrough(entity, 1.0D));
				entity.tasks.addTask(4, new EntityAITempt(entity, 1.5D, Items.CARROT_ON_A_STICK, false));
				entity.tasks.addTask(4, new EntityAITemptEdibleItem(entity, 1.5D, false));
				entity.tasks.addTask(5, new EntityAIMoveToEatItem(entity, 1.5D));
				entity.tasks.addTask(7, new EntityAIMoveToEatBlock(entity, 1.0D));
				entity.tasks.addTask(8, new EntityAIWander(entity, 1.0D));
				entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
				entity.tasks.addTask(10, new EntityAILookIdle(entity));
			}
		});

		REGISTRY.put(EntityRabbit.class, new IAIContainer<EntityAnimal>() {

			@Override
			public void registerAI(EntityAnimal entity) {
				removeAI(entity,
						Arrays.asList(new Class[] { EntityAITempt.class, EntityAIFollowParent.class,
								EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class,
								EntityAIWatchClosest.class, EntityAILookIdle.class }));
				removeAI(entity, Arrays.asList(EntityRabbit.class.getDeclaredClasses()));
				
				// this.entity.tasks.addTask(0, this.ai_crank);
				entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, 16.0F, 1.0D, 2.0D));
				entity.tasks.addTask(2, new EntityAIMateModified(entity, 2.0D));
				entity.tasks.addTask(3, new EntityAIMoveToTrough(entity, 1.0D));
				entity.tasks.addTask(4, new EntityAITempt(entity, 1.5D, Items.CARROT_ON_A_STICK, false));
				entity.tasks.addTask(4, new EntityAITemptEdibleItem(entity, 1.5D, false));
				entity.tasks.addTask(5, new EntityAIMoveToEatItem(entity, 1.5D));
				entity.tasks.addTask(7, new EntityAIMoveToEatBlock(entity, 1.0D));
				entity.tasks.addTask(8, new EntityAIWander(entity, 1.0D));
				entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
				entity.tasks.addTask(10, new EntityAILookIdle(entity));
			}
		});
		
		REGISTRY.put(EntitySheep.class, new IAIContainer<EntityAnimal>() {

			@Override
			public void registerAI(EntityAnimal entity) {
				removeAI(entity,
						Arrays.asList(new Class[] { EntityAITempt.class, EntityAIFollowParent.class,
								EntityAIWander.class, EntityAIMate.class, EntityAIPanic.class,
								EntityAIWatchClosest.class, EntityAILookIdle.class, EntityAIEatGrass.class}));

				// this.entity.tasks.addTask(0, this.ai_crank);
				entity.tasks.addTask(1, new EntityAIAvoidPlayer(entity, 16.0F, 1.0D, 2.0D));
				entity.tasks.addTask(2, new EntityAIMateModified(entity, 2.0D));
				entity.tasks.addTask(3, new EntityAIMoveToTrough(entity, 1.0D));
				entity.tasks.addTask(4, new EntityAITemptEdibleItem(entity, 1.5D, false));
				entity.tasks.addTask(5, new EntityAIMoveToEatItem(entity, 1.5D));
				entity.tasks.addTask(7, new EntityAIMoveToEatBlock(entity, 1.0D));
				entity.tasks.addTask(8, new EntityAIWander(entity, 1.0D));
				entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 6.0F));
				entity.tasks.addTask(10, new EntityAILookIdle(entity));
			}
		});
	}

	private static void removeAI(EntityLiving entity, List<Class<?>> list) {
		LinkedList<EntityAIBase> removeEntries = new LinkedList<EntityAIBase>();
		for (EntityAITaskEntry i : entity.tasks.taskEntries) {
			for (Class<?> j : list) {
				if (i.action.getClass() == j) {
					removeEntries.add(i.action);
				}
			}
		}
		for (EntityAIBase i : removeEntries) {
			entity.tasks.removeTask(i);
		}
	}

}
