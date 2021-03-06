package oortcloud.hungryanimals.potion;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import oortcloud.hungryanimals.core.lib.References;
import oortcloud.hungryanimals.core.lib.Strings;
import oortcloud.hungryanimals.entities.attributes.ModAttributes;

public class PotionDisease extends PotionHungryAnimals {

	public static ResourceLocation textureLocation = new ResourceLocation(References.MODID, "textures/potions/potiondisease.png");

	protected PotionDisease(int color) {
		super(textureLocation, true, color);
		setRegistryName(References.MODID, Strings.potionDiseaseName);
		setPotionName(Strings.potionDiseaseUnlocalizedName);
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F16089D", -0.25, 2);
		registerPotionAttributeModifier(ModAttributes.hunger_weight_bmr, "7107DE5E-7CE8-4030-940E-514C1F16089E", +4.0, 1);
	}

	@Override
	public boolean isReady(int duration, int level) {
		return true;
	}

}
