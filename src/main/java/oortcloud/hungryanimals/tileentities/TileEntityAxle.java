package oortcloud.hungryanimals.tileentities;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class TileEntityAxle extends TileEntityPowerTransporter {
	@Override
	public BlockPos[] getConnectedBlocks() {
		return new BlockPos[] {pos.up(), pos.down()};
	}
}
