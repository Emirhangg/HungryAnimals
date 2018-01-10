package oortcloud.hungryanimals.entities.ai.handler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityAnimal;

public class AIContainerTask implements IAIContainer<EntityAnimal> {

	protected LinkedList<IAIPlacer> ais;

	protected List<IAIRemover> toRemove;
	protected boolean removeAll;

	protected List<Class<? extends EntityAIBase>> prior;
	protected List<Class<? extends EntityAIBase>> posterior;

	public AIContainerTask() {
		this(null);
	}

	public AIContainerTask(AIContainerTask parent) {
		this.ais = new LinkedList<IAIPlacer>();
		this.toRemove = new LinkedList<IAIRemover>();

		if (parent != null) {
			this.removeAll = parent.removeAll;
			this.ais.addAll(parent.ais);
			this.toRemove.addAll(parent.toRemove);
		}
	}

	@Override
	public void registerAI(EntityAnimal entity) {
		if (removeAll) {
			entity.tasks.taskEntries.clear();
		} else {
			LinkedList<EntityAIBase> removeEntries = new LinkedList<EntityAIBase>();
			for (EntityAITaskEntry i : entity.tasks.taskEntries) {
				for (IAIRemover j : toRemove) {
					if (j.matches(i)) {
						removeEntries.add(i.action);
					}
				}
			}
			for (EntityAIBase i : removeEntries) {
				entity.tasks.removeTask(i);
			}
		}

		List<EntityAIBase> aibases = new ArrayList<EntityAIBase>();
		
		// Construct aibases from entity's tasks
		List<EntityAITaskEntry> aitaskentries = Lists.newArrayList(entity.tasks.taskEntries);
		aitaskentries.sort(new Comparator<EntityAITaskEntry>() {
			@Override
			public int compare(EntityAITaskEntry o1, EntityAITaskEntry o2) {
				return o1.priority - o2.priority;
			}
		});
		for (EntityAITaskEntry i : aitaskentries) {
			aibases.add(i.action);
		}
		entity.tasks.taskEntries.clear();
		
		
		for (IAIPlacer i : ais) {
			i.add(aibases, entity);
		}

		int cnt = 0;
		for (EntityAIBase i : aibases) {
			entity.tasks.addTask(cnt++, i);
		}
	}

	public void putFirst(AIFactory target) {
		ais.add(new AIPlacerFirst(target));
	}

	public void putLast(AIFactory target) {
		ais.add(new AIPlacerLast(target));
	}

	public void put(AIFactory target) {
		ais.add(new AIPlacerPriority(target, getPrior(), getPosterior()));
		prior = null;
		posterior = null;
	}

	public AIContainerTask before(Class<? extends EntityAIBase> target) {
		getPrior().add(target);
		return this;
	}

	public AIContainerTask before(List<Class<? extends EntityAIBase>> targets) {
		getPrior().addAll(targets);
		return this;
	}

	public AIContainerTask after(Class<? extends EntityAIBase> target) {
		getPosterior().add(target);
		return this;
	}

	public AIContainerTask after(List<Class<? extends EntityAIBase>> targets) {
		getPosterior().addAll(targets);
		return this;
	}

	private List<Class<? extends EntityAIBase>> getPrior() {
		if (prior == null)
			prior = new ArrayList<Class<? extends EntityAIBase>>();
		return prior;
	}

	private List<Class<? extends EntityAIBase>> getPosterior() {
		if (posterior == null)
			posterior = new ArrayList<Class<? extends EntityAIBase>>();
		return posterior;
	}

	public void remove(Class<? extends EntityAIBase> target) {
		toRemove.add(new AIRemoverByClass(target));
	}

	public void remove(IAIRemover remover) {
		toRemove.add(remover);
	}
	
	public void remove(List<Class<? extends EntityAIBase>> target) {
		toRemove.addAll(Lists.transform(target, (i) -> new AIRemoverByClass(i)));
	}

	public void removeAll() {
		removeAll = true;
	}



	public static interface IAIRemover {
		public boolean matches(EntityAITaskEntry entry);
	}
	
	public static class AIRemoverByClass implements IAIRemover {
		private Class<? extends EntityAIBase> target;
		
		public AIRemoverByClass(Class<? extends EntityAIBase> target) {
			this.target = target;
		}
		
		@Override
		public boolean matches(EntityAITaskEntry entry) {
			return entry.action.getClass() == target;
		}
	}
	
	public static class AIRemoverIsInstance implements IAIRemover {
		private Class<? extends EntityAIBase> target;
		
		public AIRemoverIsInstance(Class<? extends EntityAIBase> target) {
			this.target = target;
		}
		
		@Override
		public boolean matches(EntityAITaskEntry entry) {
			return target.isInstance(entry.action);
		}
	}
	
	protected static interface IAIPlacer {
		public boolean add(List<EntityAIBase> list, EntityAnimal entity);
	}

	protected static class AIPlacerFirst implements IAIPlacer {
		public AIFactory aiFactory;

		public AIPlacerFirst(AIFactory aiFactory) {
			this.aiFactory = aiFactory;
		}

		@Override
		public boolean add(List<EntityAIBase> list, EntityAnimal entity) {
			list.add(0, aiFactory.apply(entity));
			return true;
		}

	}

	protected static class AIPlacerLast implements IAIPlacer {
		public AIFactory aiFactory;

		public AIPlacerLast(AIFactory aiFactory) {
			this.aiFactory = aiFactory;
		}

		@Override
		public boolean add(List<EntityAIBase> list, EntityAnimal entity) {
			list.add(aiFactory.apply(entity));
			return true;
		}

	}

	protected static class AIPlacerPriority implements IAIPlacer {

		public AIFactory aiFactory;
		public List<Class<? extends EntityAIBase>> prior;
		public List<Class<? extends EntityAIBase>> posterior;

		public AIPlacerPriority(AIFactory aiFactory, List<Class<? extends EntityAIBase>> prior,
				List<Class<? extends EntityAIBase>> posterior) {
			this.aiFactory = aiFactory;
			this.prior = prior;
			this.posterior = posterior;
		}

		@Override
		public boolean add(List<EntityAIBase> list, EntityAnimal entity) {
			int maxIndex = list.size();
			int minIndex = 0;

			for (Class<? extends EntityAIBase> i : prior) {
				if (contains(list, i)) {
					minIndex = Math.max(minIndex, indexOf(list, i));
				}
			}

			for (Class<? extends EntityAIBase> i : posterior) {
				if (contains(list, i)) {
					maxIndex = Math.min(minIndex, indexOf(list, i));
				}
			}

			if (minIndex < maxIndex) {
				list.add(maxIndex, aiFactory.apply(entity));
				return true;
			} else {
				return false;
			}
		}

		private boolean contains(List<EntityAIBase> list, Class<? extends EntityAIBase> AIClass) {
			for (EntityAIBase iAIBase : list) {
				if (iAIBase.getClass() == AIClass)
					return true;
			}
			return false;
		}

		private int indexOf(List<EntityAIBase> list, Class<? extends EntityAIBase> aiClass) {
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).getClass() == aiClass) {
					return i;
				}
			}
			return -1;
		}
	}
}
