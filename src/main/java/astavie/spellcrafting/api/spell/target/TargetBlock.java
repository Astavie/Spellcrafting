package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TargetBlock implements Target {

    private final World world;
    private final BlockPos block;
    private final Vec3d pos;
    private final Direction facing;

    public TargetBlock(@NotNull World world, @NotNull BlockPos block, @NotNull Vec3d pos, @NotNull Direction facing) {
        this.world = world;
        this.block = block;
        this.pos = pos;
        this.facing = facing;
    }

    @Override
    public @NotNull BlockPos getBlock() {
        return block;
    }

    @Override
    public @NotNull Vec3d getPos() {
        return pos;
    }

    @Override
    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public @NotNull Vec3d getFacing() {
        return Vec3d.of(facing.getVector());
    }

    public Direction getDirection() {
        return facing;
    }

    @Override
    public @Nullable Caster asCaster() {
        return Caster.BLOCK_CASTER.find(world, block, facing);
    }

    @Override
    public boolean isAttuned(@NotNull Caster caster) {
        // TODO Auto-generated method stub
        return false;
    }
    
}
