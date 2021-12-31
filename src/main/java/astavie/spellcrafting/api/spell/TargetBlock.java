package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TargetBlock implements Target {

    private final World world;
    private final BlockPos block;
    private final Vec3d pos, facing;

    public TargetBlock(@NotNull World world, @NotNull BlockPos block, @NotNull Vec3d pos, @NotNull Vec3d facing) {
        this.world = world;
        this.block = block;
        this.pos = pos;
        this.facing = facing;
    }

    public TargetBlock(@NotNull World world, @NotNull BlockPos block, @NotNull Vec3d pos, @NotNull Direction facing) {
        this.world = world;
        this.block = block;
        this.pos = pos;
        this.facing = Vec3d.of(facing.getVector());
    }

    @Override
    public @Nullable Entity getEntity() {
        return null;
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
        return facing;
    }
    
}
