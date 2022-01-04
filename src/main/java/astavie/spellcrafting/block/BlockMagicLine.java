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
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class BlockMagicLine extends Block {

    public static enum InOut implements StringIdentifiable {
        IN, OUT, NONE;

        @Override
        public String asString() {
            return toString().toLowerCase();
        }
    }

    public static final EnumProperty<InOut> NORTH = EnumProperty.of("north", InOut.class);
    public static final EnumProperty<InOut> SOUTH = EnumProperty.of("south", InOut.class);
    public static final EnumProperty<InOut> EAST = EnumProperty.of("east", InOut.class);
    public static final EnumProperty<InOut> WEST = EnumProperty.of("west", InOut.class);

    public BlockMagicLine() {
        super(FabricBlockSettings.of(Material.DECORATION).noCollision().breakInstantly());
        setDefaultState(stateManager.getDefaultState().with(NORTH, InOut.OUT).with(SOUTH, InOut.IN).with(EAST, InOut.NONE).with(WEST, InOut.NONE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, EAST, WEST);
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

    private EnumProperty<InOut> getEnum(Direction dir) {
        switch (dir) {
            case NORTH:
                return NORTH;
            case SOUTH:
                return SOUTH;
            case EAST:
                return EAST;
            case WEST:
                return WEST;
            default:
                return null;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction horiz = ctx.getPlayerFacing();
        Direction in = horiz.getOpposite();

        for (Direction check : Direction.Type.HORIZONTAL) {
            if (check == horiz) continue;

            BlockState next = ctx.getWorld().getBlockState(ctx.getBlockPos().offset(check));
            if (next.getBlock() instanceof BlockMagicLine && next.get(getEnum(check.getOpposite())) == InOut.OUT) {
                in = check;
                break;
            }
        }

        return getDefaultState()
            .with(getEnum(horiz), InOut.OUT)
            .with(getEnum(horiz.rotateYClockwise()), horiz.rotateYClockwise() == in ? InOut.IN : InOut.NONE)
            .with(getEnum(horiz.getOpposite()), horiz.getOpposite() == in ? InOut.IN : InOut.NONE)
            .with(getEnum(horiz.rotateYCounterclockwise()), horiz.rotateYCounterclockwise() == in ? InOut.IN : InOut.NONE);
    }

    public Direction getSide(BlockState state, InOut inout) {
        if (state.get(NORTH) == inout) return Direction.NORTH;
        if (state.get(SOUTH) == inout) return Direction.SOUTH;
        if (state.get(WEST) == inout) return Direction.WEST;
        if (state.get(EAST) == inout) return Direction.EAST;
        return null;
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

        Direction out = getSide(state, InOut.OUT);
        Direction in = getSide(state, InOut.IN);
        if (out == null || in == null) {
            return state;
        }

        // Check for forced straight beginning
        if (out.getOpposite() == in) {
            BlockState currentOrigin = world.getBlockState(pos.offset(in));
            if (currentOrigin.getBlock() instanceof BlockMagicLine && currentOrigin.get(getEnum(in.getOpposite())) == InOut.OUT) {
                return state;
            }
        }

        // Recalculate direction
        in = out.getOpposite();

        boolean multiple = false;

        for (Direction check : Direction.Type.HORIZONTAL) {
            if (check == out) continue;

            BlockState next = world.getBlockState(pos.offset(check));
            if (next.getBlock() instanceof BlockMagicLine && next.get(getEnum(check.getOpposite())) == InOut.OUT) {
                if (multiple) {
                    in = out.getOpposite();
                    break;
                }

                in = check;
                multiple = true;
            }
        }

        return state
            .with(getEnum(out.rotateYClockwise()), out.rotateYClockwise() == in ? InOut.IN : InOut.NONE)
            .with(getEnum(out.getOpposite()), out.getOpposite() == in ? InOut.IN : InOut.NONE)
            .with(getEnum(out.rotateYCounterclockwise()), out.rotateYCounterclockwise() == in ? InOut.IN : InOut.NONE);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return getDefaultState()
            .with(NORTH, state.get(getEnum(rotation.rotate(Direction.NORTH))))
            .with(SOUTH, state.get(getEnum(rotation.rotate(Direction.SOUTH))))
            .with(EAST, state.get(getEnum(rotation.rotate(Direction.EAST))))
            .with(WEST, state.get(getEnum(rotation.rotate(Direction.WEST))));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return getDefaultState()
            .with(NORTH, state.get(getEnum(mirror.apply(Direction.NORTH))))
            .with(SOUTH, state.get(getEnum(mirror.apply(Direction.SOUTH))))
            .with(EAST, state.get(getEnum(mirror.apply(Direction.EAST))))
            .with(WEST, state.get(getEnum(mirror.apply(Direction.WEST))));
    }
    
}
