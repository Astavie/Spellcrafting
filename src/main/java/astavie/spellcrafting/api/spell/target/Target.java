package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Target {

    @NotNull
    Vec3d getPos();

    @NotNull
    BlockPos getBlock();

    @NotNull
    World getWorld();

    @NotNull
    Vec3d getFacing();

    @Nullable
    Caster asCaster();

    boolean isAttuned(@NotNull Caster caster);

}
