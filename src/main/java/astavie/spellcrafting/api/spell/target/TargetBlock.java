package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class TargetBlock implements Target {

    private final World world;
    private final BlockPos block;
    private final Vec3d pos;
    private final Direction facing;

    public static final TargetType<TargetBlock> TYPE = Registry.register(TargetType.REGISTRY, new Identifier("spellcrafting:block"), new TargetType<TargetBlock>() {

        @Override
        public TargetBlock deserialize(NbtCompound nbt, ServerWorld world) {
            BlockPos block = new BlockPos(nbt.getInt("x"), nbt.getInt("y"), nbt.getInt("z"));
            Vec3d pos = new Vec3d(nbt.getFloat("ox") + block.getX(), nbt.getFloat("oy") + block.getY(), nbt.getFloat("oz") + block.getZ());
            Direction facing = Direction.byId(nbt.getByte("side"));
            return new TargetBlock(world, block, pos, facing);
        }

        @Override
        public NbtCompound serialize(TargetBlock target) {
            NbtCompound cmp = new NbtCompound();
            cmp.putInt("x", target.block.getX());
            cmp.putInt("y", target.block.getY());
            cmp.putInt("z", target.block.getZ());
            cmp.putByte("side", (byte) target.facing.ordinal());
            cmp.putFloat("ox", (float) (target.pos.x - target.block.getX()));
            cmp.putFloat("oy", (float) (target.pos.y - target.block.getY()));
            cmp.putFloat("oz", (float) (target.pos.z - target.block.getZ()));
            return cmp;
        }
        
    });

    public TargetBlock(@NotNull World world, @NotNull BlockPos block, @NotNull Vec3d pos, @NotNull Direction facing) {
        this.world = world;
        this.block = block;
        this.pos = pos;
        this.facing = facing;
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

    public Direction getSide() {
        return facing;
    }

    @Override
    public @Nullable Caster asCaster() {
        return Caster.BLOCK_CASTER.find(world, block, facing);
    }

    @Override
    public @Nullable Attunable asAttunable() {
        return Attunable.BLOCK_ATTUNABLE.find(world, block, facing);
    }

    @Override
    public @NotNull TargetType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public @NotNull Target withPos(Vec3d pos) {
        return new TargetBlock(world, block, pos, facing);
    }
    
}
