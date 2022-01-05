package astavie.spellcrafting.block;

import java.util.Random;

import org.apache.commons.lang3.NotImplementedException;

import astavie.spellcrafting.block.entity.MagicCircleBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MagicCircleBlock extends MagicBlock implements BlockEntityProvider {

    public static int SIZE = 1;

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public IntProperty X, Y;
    public final int size;

    public MagicCircleBlock() {
        this.size = SIZE;
        if (size == 1) {
            setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH));
        } else {
            setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(X, 0).with(Y, 0));
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            MagicCircleBlockEntity circle = getBlockEntity(world, pos);
            if (circle != null) {
                int index = size == 1 ? 0 : state.get(X) + state.get(Y) * size;
                ItemStack itemStack = player.getStackInHand(hand);
                if (circle.getItem(index).isEmpty()) {
                    ItemStack insert = itemStack.copy();
                    insert.setCount(1);
                    circle.setItem(index, insert);

                    itemStack.decrement(1);
                    player.setStackInHand(hand, itemStack);
                    return ActionResult.SUCCESS;
                } else if (
                    itemStack.isEmpty() ||
                    (ItemStack.canCombine(circle.getItem(index), itemStack) && itemStack.getCount() < itemStack.getMaxCount())
                ) {
                    ItemStack remove = circle.getItem(index);
                    circle.setItem(index, ItemStack.EMPTY);

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
        Direction right = ctx.getPlayerFacing();
        Direction down = right.rotateYClockwise();

        BlockState state = getDefaultState().with(FACING, right);

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (y == 0 && x == 0) continue;

                BlockPos blockPos = ctx.getBlockPos().offset(right, x).offset(down, y);
                World world = ctx.getWorld();
                if (!world.getBlockState(blockPos).canReplace(ctx) || !state.canPlaceAt(ctx.getWorld(), blockPos)) {
                    return null;
                }
            }
        }

        return state;
    }

    private BlockState getCircleState(Direction dir, int x, int y) {
        if (size == 1) {
            return getDefaultState().with(FACING, dir);
        } else {
            return getDefaultState().with(FACING, dir).with(X, x).with(Y, y);
        }
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        Direction right = state.get(FACING);
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (y == 0 && x == 0) continue;

                BlockPos blockPos = pos.offset(right, x).offset(down, y);
                world.setBlockState(blockPos, getCircleState(right, x, y), Block.NOTIFY_ALL | Block.FORCE_STATE);
            }
        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.isOf(newState.getBlock())) {
            return;
        }

        MagicCircleBlockEntity circle = (MagicCircleBlockEntity) world.getBlockEntity(pos);
        if (circle == null) return;

        Direction right = state.get(FACING);
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                BlockPos pos2 = circle.getPos().offset(right, x).offset(down, y);
                ItemScatterer.spawn(world, pos2.getX(), pos2.getY(), pos2.getZ(), circle.getItem(x + y * size));
            }
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canPlaceAt(world, pos)) {
            world.createAndScheduleBlockTick(pos, this, 1);
        }

        MagicCircleBlockEntity circle = getBlockEntity(world, pos);
        if (circle == null) return Blocks.AIR.getDefaultState();

        if (size == 1) return state;

        Direction right = state.get(FACING);
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                BlockPos pos2 = circle.getPos().offset(right, x).offset(down, y);
                BlockState state2 = world.getBlockState(pos2);

                if (
                    state2.getBlock() != this ||
                    state2.get(FACING) != right ||
                    state2.get(X) != x ||
                    state2.get(Y) != y
                ) {
                    return Blocks.AIR.getDefaultState();
                }
            }
        }

        return state;
    }

    public MagicCircleBlockEntity getBlockEntity(WorldAccess world, BlockPos pos) {
        if (size == 1) return (MagicCircleBlockEntity) world.getBlockEntity(pos);

        BlockState state = world.getBlockState(pos);

        Direction right = state.get(FACING);
        Direction left = right.getOpposite();
        Direction up = right.rotateYCounterclockwise();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                BlockPos p = pos.offset(up, i).offset(left, j);
                BlockState s = world.getBlockState(p);
                if (s.getBlock() == this && s.get(X) == 0 && s.get(Y) == 0) {
                    return (MagicCircleBlockEntity) world.getBlockEntity(p);
                }
            }
        }

        return null;
    }

    @Override
    public Direction[] getOutputs(WorldView world, BlockPos pos, BlockState state) {
        throw new NotImplementedException();
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        if (X == null || Y == null || (state.get(X) == 0 && state.get(Y) == 0)) {
            return new MagicCircleBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING);
        if (SIZE > 1) {
            builder.add(X = IntProperty.of("x", 0, SIZE - 1));
            builder.add(Y = IntProperty.of("y", 0, SIZE - 1));
        }
    }

    @Override
    public boolean isOutput(WorldView world, BlockPos pos, BlockState state, Direction side) {
        if (size == 1) {
            return side == state.get(FACING);
        }
        return side == state.get(FACING) && state.get(X) == size - 1;
    }

    @Override
    public boolean isInput(WorldView world, BlockPos pos, BlockState state, Direction side) {
        if (size == 1) {
            return side == state.get(FACING).getOpposite();
        }
        return side == state.get(FACING).getOpposite() && state.get(X) == 0;
    }
    
}
