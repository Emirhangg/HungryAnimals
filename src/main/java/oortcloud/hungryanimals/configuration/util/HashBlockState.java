package oortcloud.hungryanimals.configuration.util;

import com.google.common.collect.ImmutableSet;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class HashBlockState {
	private IBlockState block;
	private boolean ignoreProperty;

	public HashBlockState(Block block) {
		this(block.getDefaultState(), true);
	}

	public HashBlockState(IBlockState block) {
		this(block, false);
	}

	public HashBlockState(IBlockState block, boolean ignoreProperty) {
		this.block = block;
		this.ignoreProperty = ignoreProperty;
	}

	@Override
	public boolean equals(Object obj) {
		if (this.ignoreProperty && ((HashBlockState) obj).ignoreProperty) {
			return block.getBlock() == ((HashBlockState) obj).block.getBlock();
		} else if (!this.ignoreProperty && !((HashBlockState) obj).ignoreProperty) {

			if (block.getBlock() != ((HashBlockState) obj).block.getBlock())
				return false;

			for (Object i : block.getProperties().keySet()) {
				IProperty property = (IProperty) i;
				if (!block.getValue(property).equals(((HashBlockState) obj).block.getValue(property)))
					return false;
			}

			return true;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		if (ignoreProperty) {
			return block.getBlock().hashCode();
		} else {
			return block.hashCode();
		}
	}
	
	public String toString() {
		if (ignoreProperty) {
			return "("+String.valueOf(Block.REGISTRY.getNameForObject(block.getBlock()))+")";
		} else {
			ImmutableSet properties = block.getProperties().keySet();
			String[] propertyStrings = new String[properties.size()];
			int next = 0;
			for (Object i : properties) {
				IProperty property = (IProperty)i;
				propertyStrings[next++] = "(" + property.getName() + "," + block.getValue(property) + ")";
			}
			
			String propertyString = new String();
			propertyString+="("+propertyStrings[0];
			for (int i = 1; i < propertyStrings.length; i++) {
				propertyString+=","+propertyStrings[i];
			}
			propertyString+=")";
			return "("+String.valueOf(Block.REGISTRY.getNameForObject(block.getBlock()))+","+propertyString+")";
		}
	}
}
