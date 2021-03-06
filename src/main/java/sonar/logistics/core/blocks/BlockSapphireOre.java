package sonar.logistics.core.blocks;

import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import sonar.core.SonarCore;
import sonar.logistics.PL2Items;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockSapphireOre extends BlockOre {

	public BlockSapphireOre() {
		setHarvestLevel("pickaxe", 2);
	}

    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune){
		return PL2Items.sapphire;
	}

	public int quantityDropped(@Nonnull Random rand) {
		return 1;
	}

	@Override
	public int getExpDrop(@Nonnull IBlockState state, net.minecraft.world.IBlockAccess world, BlockPos pos, int fortune) {
		return MathHelper.getInt(SonarCore.rand, 0, 1);
	}
}
