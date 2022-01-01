package astavie.spellcrafting.api.spell.target;

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
    private final Vec3d pos, facing, origin;

    public TargetBlock(@NotNull World world, @NotNull BlockPos block, @NotNull Vec3d pos, @NotNull Vec3d facing, @NotNull Vec3d origin) {
        this.world = world;
        this.block = block;
        this.pos = pos;
        this.facing = facing;
        this.origin = origin;
    }

    public TargetBlock(@NotNull World world, @NotNull BlockPos block, @NotNull Vec3d pos, @NotNull Direction facing, @NotNull Vec3d origin) {
        this.world = world;
        this.block = block;
        this.pos = pos;
        this.facing = Vec3d.of(facing.getVector());
        this.origin = origin;
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

    @Override
    public @NotNull Vec3d getOrigin() {
        return origin;
    }
    
}
