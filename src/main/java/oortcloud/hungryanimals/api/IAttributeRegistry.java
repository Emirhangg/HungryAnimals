package oortcloud.hungryanimals.api;

import net.minecraft.entity.ai.attributes.IAttribute;

public interface IAttributeRegistry {
	public boolean registerAttribute(String name, IAttribute attribute, boolean shouldRegister);
}
