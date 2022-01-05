package astavie.spellcrafting.block;

import java.util.Random;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
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

public class BlockMagicLine extends Block {

    private static enum Output implements StringIdentifiable {
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
                    if (direction == in.rotateYClockwise()) return CROSS;
                    break;
                case T_RIGHT:
                    if (direction == in.rotateYCounterclockwise()) return CROSS;
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

    public BlockMagicLine() {
        super(FabricBlockSettings.of(Material.DECORATION).noCollision().breakInstantly());
        setDefaultState(stateManager.getDefaultState().with(IN, Direction.SOUTH).with(OUT, Output.STRAIGHT));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(IN, OUT);
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockPos blockPos = pos.down();
        BlockState blockState = world.getBlockState(blockPos);
        return this.canRunOnTop(world, blockPos, blockState);
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP);
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

        for (Direction check : Direction.Type.HORIZONTAL) {
            if (check == horiz) continue;

            BlockState next = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(check));
            if (next.getBlock() == this && next.get(OUT).isOutput(next.get(IN), check.getOpposite())) {
                in = check;
                out = Output.withDirection(null, in, horiz);
                break;
            }
        }

        return getDefaultState().with(IN, in).with(OUT, out);
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
        if (currentOrigin.getBlock() == this && currentOrigin.get(OUT).isOutput(currentOrigin.get(IN), in.getOpposite())) {
            return state;
        }

        // Recalculate direction
        for (Direction check : Direction.Type.HORIZONTAL) {
            if (out.isOutput(in, check)) continue;

            BlockState next = world.getBlockState(pos.offset(check));
            if (next.getBlock() == this && next.get(OUT).isOutput(next.get(IN), check.getOpposite())) {
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
    
}
