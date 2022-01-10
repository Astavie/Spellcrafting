package astavie.spellcrafting.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public abstract class MagicBlock extends Block {

    public static enum Status implements StringIdentifiable {
        NONE(0), OFF(0x7F65CC), READY(0x9F7EFF), CASTING(0x7EF9FF), SACRIFICE(0x7EFF7E), ERROR(0xFF7E7E);

        public final int color;

        private Status(int color) {
            this.color = color;
        }

        public static Status[] getOn() {
            return new Status [] { OFF, READY, CASTING, SACRIFICE, ERROR };
        }

        @Override
        public String asString() {
            return toString().toLowerCase();
        }
    }

    public static final EnumProperty<Status> STATUS = EnumProperty.of("status", Status.class, Status.getOn());

    public MagicBlock() {
        super(FabricBlockSettings.of(Material.DECORATION).noCollision().breakInstantly());
    }

    public abstract boolean isOutput(WorldView world, BlockPos pos, BlockState state, Direction side);

    public abstract Status getOutputState(WorldView world, BlockPos pos, BlockState state, Direction side);

    public abstract Direction[] getOutputs(WorldView world, BlockPos pos, BlockState state);

    public abstract boolean isInput(WorldView world, BlockPos pos, BlockState state, Direction side);

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
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
    
}
