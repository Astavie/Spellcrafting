package astavie.spellcrafting.api.spell.target;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.util.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;

public class TargetEntity implements Target {

    private final RegistryKey<World> dimension;
    private final UUID uuid;
    private final Vec3f offset;

    public static final TargetType<TargetEntity> TYPE = Registry.register(TargetType.REGISTRY, new Identifier("spellcrafting:entity"), new TargetType<TargetEntity>() {

        @Override
        public TargetEntity deserialize(NbtCompound nbt) {
            RegistryKey<World> dimension = RegistryKey.of(Registry.WORLD_KEY, new Identifier(nbt.getString("dim")));
            UUID uuid = nbt.getUuid("UUID");
            Vec3f offset = new Vec3f(nbt.getFloat("ox"), nbt.getFloat("oy"), nbt.getFloat("oz"));
            return new TargetEntity(dimension, uuid, offset);
        }

        @Override
        public NbtCompound serialize(TargetEntity target) {
            NbtCompound cmp = new NbtCompound();
            cmp.putString("dim", target.dimension.getValue().toString());
            cmp.putUuid("UUID", target.uuid);
            cmp.putFloat("ox", target.offset.getX());
            cmp.putFloat("oy", target.offset.getY());
            cmp.putFloat("oz", target.offset.getZ());
            return cmp;
        }
        
    });

    private TargetEntity(RegistryKey<World> dimension, UUID uuid, Vec3f offset) {
        this.dimension = dimension;
        this.uuid = uuid;
        this.offset = offset;
    }

    public TargetEntity(@NotNull Entity entity, @NotNull Vec3d pos) {
        this.dimension = entity.world.getRegistryKey();
        this.uuid = entity.getUuid();
        this.offset = new Vec3f(pos.subtract(entity.getPos()));
    }

    public @NotNull Entity getEntity() {
        return getWorld().getEntity(uuid);
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
    public @NotNull ServerWorld getWorld() {
        return ServerUtils.server.getWorld(dimension);
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

    @Override
    public @NotNull Target withPos(Vec3d pos) {
        return new TargetEntity(getEntity(), pos);
    }
    
}
