package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public interface Target {

    boolean exists();

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

    @Nullable
    Attunable asAttunable();

    @NotNull
    TargetType<?> getType();

    public static NbtCompound serialize(Target t) {
        NbtCompound c = serialize(t.getType(), t);
        c.putString("type", TargetType.REGISTRY.getId(t.getType()).toString());
        return c;
    }

    public static Target deserialize(NbtCompound c, ServerWorld world) {
        TargetType<?> type = TargetType.REGISTRY.get(new Identifier(c.getString("type")));
        return type.deserialize(c, world);
    }

    @SuppressWarnings("unchecked")
    private static <T extends Target> NbtCompound serialize(TargetType<T> type, Target t) {
        return type.serialize((T) t);
    }

}
