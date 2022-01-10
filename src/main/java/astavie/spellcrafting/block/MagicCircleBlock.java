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
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MagicCircleBlock extends MagicBlock implements BlockEntityProvider {

    public static int SIZE = 1;

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public IntProperty X, Y;
    public final int size;

    public static final EnumProperty<Status> CONNECTED = EnumProperty.of("io", Status.class);
    public static final EnumProperty<Status> INPUT = EnumProperty.of("input", Status.class);
    public static final EnumProperty<Status> OUTPUT = EnumProperty.of("output", Status.class);

    public MagicCircleBlock() {
        this.size = SIZE;
        if (size == 1) {
            setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(STATUS, Status.OFF).with(INPUT, Status.NONE).with(OUTPUT, Status.NONE));
        } else {
            setDefaultState(stateManager.getDefaultState().with(FACING, Direction.NORTH).with(STATUS, Status.OFF).with(X, 0).with(Y, 0).with(CONNECTED, Status.NONE));
        }
    }

    public void setStatus(World world, BlockPos pos, BlockState state, Status status) {
        Direction right = state.get(FACING);
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                BlockPos pos2 = pos.offset(right, x).offset(down, y);
                world.setBlockState(pos2, world.getBlockState(pos2).with(STATUS, status));
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient) {
            MagicCircleBlockEntity circle = getBlockEntity(world, pos);
            if (circle != null) {
                ItemStack itemStack = player.getStackInHand(hand);
                if (itemStack.isEmpty()) {
                    ItemStack remove = circle.popItem(); 
                    player.setStackInHand(hand, remove);
                } else {
                    ItemStack insert = itemStack.copy();
                    insert.setCount(1);
                    if (circle.pushItem(insert)) {
                        itemStack.decrement(1);
                        player.setStackInHand(hand, itemStack);
                    }
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction right = ctx.getPlayerFacing();
        Direction down = right.rotateYClockwise();

        BlockState state = getCircleState(ctx.getWorld(), ctx.getBlockPos(), right, Status.OFF, 0, 0);

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

    private BlockState getCircleState(WorldView world, BlockPos pos, Direction dir, Status status, int x, int y) {
        BlockState state = getDefaultState().with(FACING, dir).with(STATUS, status);

        if (size > 1) {
            state = state.with(X, x).with(Y, y);
        }

        if (x == 0) {
            BlockPos left = pos.offset(dir, -1);
            BlockState leftState = world.getBlockState(left);
            if (leftState.getBlock() instanceof MagicBlock magic) {
                state = state.with(size == 1 ? INPUT : CONNECTED, magic.getOutputState(world, left, leftState, dir));
            }
        }

        if (x == size - 1) {
            BlockPos right = pos.offset(dir);
            BlockState rightState = world.getBlockState(right);
            if (rightState.getBlock() instanceof MagicBlock magic && magic.isInput(world, right, rightState, dir.getOpposite())) {
                state = state.with(size == 1 ? OUTPUT : CONNECTED, getOutputState(world, pos, state, dir));
            }
        }

        return state;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        Direction right = state.get(FACING);
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (y == 0 && x == 0) continue;

                BlockPos blockPos = pos.offset(right, x).offset(down, y);
                world.setBlockState(blockPos, getCircleState(world, blockPos, right, Status.OFF, x, y), Block.NOTIFY_ALL | Block.FORCE_STATE);
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
                ItemScatterer.spawn(world, pos2.getX(), pos2.getY(), pos2.getZ(), circle.popItem());
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

        Direction right = state.get(FACING);
        if (size == 1) return getCircleState(world, pos, right, state.get(STATUS), 0, 0);

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

        return getCircleState(world, pos, right, state.get(STATUS), state.get(X), state.get(Y));
    }

    public MagicCircleBlockEntity getBlockEntity(WorldAccess world, BlockPos pos) {
        if (size == 1) return (MagicCircleBlockEntity) world.getBlockEntity(pos);

        BlockState state = world.getBlockState(pos);

        Direction right = state.get(FACING);
        Direction down = right.rotateYClockwise();

        BlockPos p = pos.offset(down, -state.get(Y)).offset(right, -state.get(X));
        return (MagicCircleBlockEntity) world.getBlockEntity(p);
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
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (size > 1 && (state.get(X) > 0 || state.get(Y) > 0)) return;

        Status status = state.get(STATUS);
        if (status == Status.OFF) return;

        float px = (pos.getX() + pos.offset(state.get(FACING), size - 1).offset(state.get(FACING).rotateYClockwise(), size - 1).getX()) / 2f + 0.5f;
        float pz = (pos.getZ() + pos.offset(state.get(FACING), size - 1).offset(state.get(FACING).rotateYClockwise(), size - 1).getZ()) / 2f + 0.5f;

        for (int i = 0; i < size; i++) {
            float r = size / 2f - (3f / 16) + (random.nextFloat() - 0.5f) * (4f / 16);
            double a = random.nextDouble() * (float) Math.PI * 2;
            float x = r * (float) Math.cos(a);
            float y = r * (float) Math.sin(a);

            world.addParticle(new DustParticleEffect(new Vec3f(Vec3d.unpackRgb(status.color)), 1.0f), px + x, pos.getY() + 2f / 16, pz + y, 0, 0, 0);
        }
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(FACING, STATUS);
        if (SIZE > 1) {
            builder.add(X = IntProperty.of("x", 0, SIZE - 1));
            builder.add(Y = IntProperty.of("y", 0, SIZE - 1));
            builder.add(CONNECTED);
        } else {
            builder.add(INPUT, OUTPUT);
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

    @Override
    public Status getOutputState(WorldView world, BlockPos pos, BlockState state, Direction side) {
        if (side == state.get(FACING) && (size == 1 || state.get(X) == size - 1)) {
            return Status.OFF; // TODO: Output state
        } else {
            return Status.NONE;
        }
    }
    
}
