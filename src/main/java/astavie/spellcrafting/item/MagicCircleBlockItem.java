package astavie.spellcrafting.item;

import astavie.spellcrafting.block.MagicCircleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MagicCircleBlockItem extends BlockItem {

    private final MagicCircleBlock block;

    public MagicCircleBlockItem(MagicCircleBlock block, Settings settings) {
        super(block, settings);
        this.block = block;
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        Direction right = context.getPlayerFacing();
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < block.size; y++) {
            for (int x = 0; x < block.size; x++) {
                if (y == 0 && x == 0) continue;
                BlockPos blockPos = context.getBlockPos().offset(right, x).offset(down, y);
                World world = context.getWorld();
                BlockState blockState = world.isWater(blockPos) ? Blocks.WATER.getDefaultState() : Blocks.AIR.getDefaultState();
                world.setBlockState(blockPos, blockState, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD | Block.FORCE_STATE);
            }
        }

        return super.place(context, state);
    }
    
}
