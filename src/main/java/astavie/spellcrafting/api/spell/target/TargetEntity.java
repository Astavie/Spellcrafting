package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TargetEntity implements Target {

    private final Entity entity;
    private final Vec3d offset;

    public TargetEntity(@NotNull Entity entity, @NotNull Vec3d pos) {
        this.entity = entity;
        this.offset = pos.subtract(entity.getPos());
    }

    public @NotNull Entity getEntity() {
        return entity;
    }

    @Override
    public @NotNull BlockPos getBlock() {
        return new BlockPos(getPos());
    }

    @Override
    public @NotNull Vec3d getPos() {
        return entity.getPos().add(offset);
    }

    @Override
    public @NotNull World getWorld() {
        return entity.world;
    }

    @Override
    public @NotNull Vec3d getFacing() {
        return Vec3d.fromPolar(entity.getPitch(), entity.getHeadYaw());
    }

    @Override
    public @Nullable Caster asCaster() {
        return Caster.ENTITY_CASTER.find(entity, null);
    }

    @Override
    public @Nullable Attunable asAttunable() {
        return Attunable.ENTITY_ATTUNABLE.find(entity, null);
    }
    
}
