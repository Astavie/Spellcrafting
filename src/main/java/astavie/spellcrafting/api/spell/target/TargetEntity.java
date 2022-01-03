package astavie.spellcrafting.api.spell.target;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TargetEntity implements Target {

    private final ServerWorld world;
    private final UUID uuid;
    private final Vec3f offset;

    public static final TargetType<TargetEntity> TYPE = Registry.register(TargetType.REGISTRY, new Identifier("spellcrafting:entity"), new TargetType<TargetEntity>() {

        @Override
        public TargetEntity deserialize(NbtCompound nbt, ServerWorld world) {
            UUID uuid = nbt.getUuid("UUID");
            Vec3f offset = new Vec3f(nbt.getFloat("ox"), nbt.getFloat("oy"), nbt.getFloat("oz"));
            return new TargetEntity(world, uuid, offset);
        }

        @Override
        public NbtCompound serialize(TargetEntity target) {
            NbtCompound cmp = new NbtCompound();
            cmp.putUuid("UUID", target.uuid);
            cmp.putFloat("ox", target.offset.getX());
            cmp.putFloat("oy", target.offset.getY());
            cmp.putFloat("oz", target.offset.getZ());
            return cmp;
        }
        
    });

    private TargetEntity(ServerWorld world, UUID uuid, Vec3f offset) {
        this.world = world;
        this.uuid = uuid;
        this.offset = offset;
    }

    public TargetEntity(@NotNull Entity entity, @NotNull Vec3d pos) {
        this.world = (ServerWorld) entity.world;
        this.uuid = entity.getUuid();
        this.offset = new Vec3f(pos.subtract(entity.getPos()));
    }

    public @NotNull Entity getEntity() {
        return world.getEntity(uuid);
    }

    @Override
    public @NotNull BlockPos getBlock() {
        return new BlockPos(getPos());
    }

    @Override
    public @NotNull Vec3d getPos() {
        return getEntity().getPos().add(new Vec3d(offset));
    }

    @Override
    public @NotNull World getWorld() {
        return world;
    }

    @Override
    public @Nullable Caster asCaster() {
        return Caster.ENTITY_CASTER.find(getEntity(), null);
    }

    @Override
    public @Nullable Attunable asAttunable() {
        return Attunable.ENTITY_ATTUNABLE.find(getEntity(), null);
    }

    @Override
    public @NotNull TargetType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean exists() {
        return getEntity() != null;
    }
    
}
