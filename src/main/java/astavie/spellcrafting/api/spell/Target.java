package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Target {
    
    @Nullable
    Entity getEntity();

    @NotNull
    BlockPos getBlock();

    @NotNull
    Vec3d getPos();

    @NotNull
    World getWorld();

    @NotNull
    Vec3d getFacing();

    @NotNull
    default Direction getDirection() {
        Vec3d facing = getFacing();
        return Direction.getFacing(facing.x, facing.y, facing.z);
    }

    @Nullable
    default Caster asCaster() {
        Entity entity = getEntity();
        if (entity != null) {
            return Caster.ENTITY_CASTER.find(entity, null);
        }
        return Caster.BLOCK_CASTER.find(getWorld(), getBlock(), getDirection());
    }

}
