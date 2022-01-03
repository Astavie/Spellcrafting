package astavie.spellcrafting.api.spell.target;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public record TargetRaycast(World world, Vec3d pos) implements Target {

    public static final TargetType<TargetRaycast> TYPE = Registry.register(TargetType.REGISTRY, new Identifier("spellcrafting:raycast"), new TargetType<TargetRaycast>() {

        @Override
        public TargetRaycast deserialize(NbtCompound nbt, ServerWorld world) {
            Vec3d pos = new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
            return new TargetRaycast(world, pos);
        }

        @Override
        public NbtCompound serialize(TargetRaycast target) {
            NbtCompound cmp = new NbtCompound();
            cmp.putDouble("x", target.pos.getX());
            cmp.putDouble("y", target.pos.getY());
            cmp.putDouble("z", target.pos.getZ());
            return cmp;
        }
        
    });

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public @NotNull Vec3d getPos() {
        return pos;
    }

    @Override
    public @NotNull BlockPos getBlock() {
        return new BlockPos(pos);
    }

    @Override
    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public @Nullable Caster asCaster() {
        return null;
    }

    @Override
    public @Nullable Attunable asAttunable() {
        // Attuned? yes.
        return new Attunable() {

            @Override
            public boolean isAttunedTo(@NotNull Attunable attunable) {
                return true;
            }

            @Override
            public void attuneTo(@Nullable Attunable attunable) {
            }

            @Override
            public @NotNull UUID getAttunement() {
                return UUID.randomUUID();
            }

            @Override
            public @NotNull Vec3d getCenter() {
                return pos;
            }
            
        };
    }

    @Override
    public @NotNull TargetType<?> getType() {
        return TYPE;
    }
    
}
