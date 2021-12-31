package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TargetEntity implements Target {

    private final Entity entity;
    private final Vec3d pos;

    public TargetEntity(@NotNull Entity entity, @NotNull Vec3d pos) {
        this.pos = pos;
        this.entity = entity;
    }

    @Override
    public @Nullable Entity getEntity() {
        return entity;
    }

    @Override
    public @NotNull BlockPos getBlock() {
        return new BlockPos(getPos());
    }

    @Override
    public @NotNull Vec3d getPos() {
        return pos;
    }

    @Override
    public @NotNull World getWorld() {
        return entity.world;
    }

    @Override
    public @NotNull Vec3d getFacing() {
        return Vec3d.fromPolar(entity.getPitch(), entity.getHeadYaw());
    }
    
}
