package astavie.spellcrafting.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class MagicLineBlock extends MagicBlock {

    public static enum Output implements StringIdentifiable {
        LEFT, RIGHT, STRAIGHT, T_LEFT, T_RIGHT, T_SPLIT, CROSS;

        @Override
        public String asString() {
            return toString().toLowerCase();
        }

        public boolean isOutput(Direction in, Direction direction) {
            if (in == direction || direction.getAxis() == Axis.Y) return false;

            switch (this) {
                case LEFT: return direction == in.rotateYClockwise();
                case RIGHT: return direction == in.rotateYCounterclockwise();
                case STRAIGHT: return direction == in.getOpposite();
                case T_LEFT: return direction != in.rotateYCounterclockwise();
                case T_RIGHT: return direction != in.rotateYClockwise();
                case T_SPLIT: return direction != in.getOpposite();
                case CROSS: return true;
            }

            throw new IllegalStateException();
        }

        public Direction[] getOutputs(Direction in) {
            switch (this) {
                case LEFT: return new Direction[] { in.rotateYClockwise() };
                case RIGHT: return new Direction[] { in.rotateYCounterclockwise() };
                case STRAIGHT: return new Direction[] { in.getOpposite() };
                case T_LEFT: return new Direction[] { in.rotateYClockwise(), in.getOpposite() };
                case T_RIGHT: return new Direction[] { in.rotateYCounterclockwise(), in.getOpposite() };
                case T_SPLIT: return new Direction[] { in.rotateYClockwise(), in.rotateYCounterclockwise() };
                case CROSS: return new Direction[] { in.rotateYClockwise(), in.rotateYCounterclockwise(), in.getOpposite() };
            }

            throw new IllegalStateException();
        }

        public static Output withDirection(Output out, Direction in, Direction direction) {
            if (out == null) {
                if (direction == in.getOpposite()) return STRAIGHT;
                if (direction == in.rotateYCounterclockwise()) return RIGHT;
                if (direction == in.rotateYClockwise()) return LEFT;
                return null;
            }

            switch (out) {
                case LEFT:
                    if (direction == in.getOpposite()) return T_LEFT;
                    if (direction == in.rotateYCounterclockwise()) return T_SPLIT;
                    break;
                case RIGHT:
                    if (direction == in.getOpposite()) return T_RIGHT;
                    if (direction == in.rotateYClockwise()) return T_SPLIT;
                    break;
                case STRAIGHT:
                    if (direction == in.rotateYCounterclockwise()) return T_RIGHT;
                    if (direction == in.rotateYClockwise()) return T_LEFT;
                    break;
                case T_LEFT:
                    if (direction == in.rotateYCounterclockwise()) return CROSS;
                    break;
                case T_RIGHT:
                    if (direction == in.rotateYClockwise()) return CROSS;
                    break;
                case T_SPLIT:
                    if (direction == in.getOpposite()) return CROSS;
                    break;
                case CROSS:
                    break;
            }

            return out;
        }
    }

    public static final DirectionProperty IN = DirectionProperty.of("in", Direction.Type.HORIZONTAL);
    public static final EnumProperty<Output> OUT = EnumProperty.of("out", Output.class);

    public MagicLineBlock() {
        setDefaultState(stateManager.getDefaultState().with(IN, Direction.SOUTH).with(OUT, Output.STRAIGHT).with(STATUS, Status.OFF));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(IN, OUT, STATUS);
    }

    public ActionResult onChalk(World world, BlockPos pos, BlockState state, Direction direction) {
        Output current = state.get(OUT);
        Output with = Output.withDirection(current, state.get(IN), direction);
        if (current != with) {
            world.setBlockState(pos, state.with(OUT, with), Block.NOTIFY_LISTENERS);
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction horiz = ctx.getPlayerFacing();
        Direction in = horiz.getOpposite();
        Output out = Output.STRAIGHT;
        Status status = Status.OFF;

        for (Direction check : Direction.Type.HORIZONTAL) {
            if (check == horiz) continue;

            BlockPos pos = ctx.getBlockPos().offset(check);
            BlockState next = ctx.getWorld().getBlockState(pos);
            if (next.getBlock() instanceof MagicBlock magic && magic.isOutput(ctx.getWorld(), pos, next, check.getOpposite())) {
                in = check;
                out = Output.withDirection(null, in, horiz);
                status = magic.getOutputState(ctx.getWorld(), pos, next, check.getOpposite());
                break;
            }
        }

        return getDefaultState().with(IN, in).with(OUT, out).with(STATUS, status);
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

        Direction in = state.get(IN);
        Output out = state.get(OUT);

        // Check for forced
        BlockState currentOrigin = world.getBlockState(pos.offset(in));
        if (currentOrigin.getBlock() instanceof MagicBlock magic && magic.isOutput(world, pos.offset(in), currentOrigin, in.getOpposite())) {
            return state.with(STATUS, magic.getOutputState(world, pos.offset(in), currentOrigin, in.getOpposite()));
        }

        state = state.with(STATUS, Status.OFF);

        // Recalculate direction
        for (Direction check : Direction.Type.HORIZONTAL) {
            if (out.isOutput(in, check)) continue;

            BlockState next = world.getBlockState(pos.offset(check));
            if (next.getBlock() instanceof MagicBlock && ((MagicBlock) next.getBlock()).isOutput(world, pos.offset(check), next, check.getOpposite())) {
                Output ret = null;
                for (Direction dir : Direction.Type.HORIZONTAL) {
                    if (out.isOutput(in, dir)) {
                        ret = Output.withDirection(ret, check, dir);
                    }
                }
                return state.with(IN, check).with(OUT, ret);
            }
        }

        return state;
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return getDefaultState().with(IN, rotation.rotate(state.get(IN)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return getDefaultState().with(IN, mirror.apply(state.get(IN)));
    }

    @Override
    public boolean isOutput(WorldView world, BlockPos pos, BlockState state, Direction side) {
        return state.get(OUT).isOutput(state.get(IN), side);
    }

    @Override
    public boolean isInput(WorldView world, BlockPos pos, BlockState state, Direction side) {
        return state.get(IN) == side;
    }

    @Override
    public Direction[] getOutputs(WorldView world, BlockPos pos, BlockState state) {
        return state.get(OUT).getOutputs(state.get(IN));
    }

    @Override
    public Status getOutputState(WorldView world, BlockPos pos, BlockState state, Direction side) {
        return isOutput(world, pos, state, side) ? state.get(STATUS) : Status.NONE;
    }
    
}
