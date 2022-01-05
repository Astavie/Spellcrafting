package astavie.spellcrafting.block;

import astavie.spellcrafting.block.entity.MagicCircleBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class MagicCircle1x1Block extends MagicBlock implements BlockEntityProvider {

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;

    public MagicCircle1x1Block() {
        setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity entity = world.getBlockEntity(pos);
            if (entity instanceof MagicCircleBlockEntity) {
                MagicCircleBlockEntity circle = (MagicCircleBlockEntity) entity;
                ItemStack itemStack = player.getStackInHand(hand);
                if (circle.getItem().isEmpty()) {
                    ItemStack insert = itemStack.copy();
                    insert.setCount(1);
                    circle.setItem(insert);

                    itemStack.decrement(1);
                    player.setStackInHand(hand, itemStack);
                    return ActionResult.SUCCESS;
                } else if (
                    itemStack.isEmpty() ||
                    (ItemStack.canCombine(circle.getItem(), itemStack) && itemStack.getCount() < itemStack.getMaxCount())
                ) {
                    ItemStack remove = circle.getItem();
                    circle.setItem(ItemStack.EMPTY);

                    remove.setCount(itemStack.getCount() + 1);
                    player.setStackInHand(hand, remove);
                    return ActionResult.SUCCESS;
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerFacing());
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public boolean isOutput(WorldAccess world, BlockPos pos, BlockState state, Direction side) {
        return side == state.get(FACING);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos var1, BlockState var2) {
        return new MagicCircleBlockEntity(var1, var2);
    }

}
