package astavie.spellcrafting.api.spell;

import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;

public interface SpellType<T> {

    public static final @NotNull SpellType<Object> ANY = new SpellType<Object>() {

        @Override
        public @NotNull Class<Object> getValueClass() {
            return Object.class;
        }

        @Override
        public @NotNull DyeColor getColor() {
            return DyeColor.WHITE;
        }

        @Override
        public @NotNull Object deserialize(@NotNull NbtElement nbt, ServerWorld world) {
            throw new NotImplementedException();
        }

        @Override
        public @NotNull NbtElement serialize(@NotNull Object t) {
            throw new NotImplementedException();
        }

        @Override
        public boolean exists(@NotNull Object t) {
            throw new NotImplementedException();
        }

    };

    public static final @NotNull SpellType<Void> NONE = new SpellType<Void>() {

        @Override
        public @NotNull Class<Void> getValueClass() {
            return Void.class;
        }

        @Override
        public @NotNull DyeColor getColor() {
            return DyeColor.WHITE;
        }

        @Override
        public @NotNull Void deserialize(@NotNull NbtElement nbt, ServerWorld world) {
            throw new NotImplementedException();
        }

        @Override
        public @NotNull NbtElement serialize(@NotNull Void t) {
            throw new NotImplementedException();
        }

        @Override
        public boolean exists(@NotNull Void t) {
            throw new NotImplementedException();
        }

    };

    public static final @NotNull SpellType<DistancedTarget> TARGET = new SpellType<DistancedTarget>() {

        @Override
        public @NotNull Class<DistancedTarget> getValueClass() {
            return DistancedTarget.class;
        }

        @Override
        public @NotNull DyeColor getColor() {
            return DyeColor.BLUE;
        }

        @Override
        public @NotNull DistancedTarget deserialize(@NotNull NbtElement nbt, ServerWorld world) {
            NbtCompound cmp = (NbtCompound) nbt;
            Target target = Target.deserialize(cmp.getCompound("target"), world);
            Target origin = null;
            if (cmp.contains("origin")) {
                origin = Target.deserialize(cmp.getCompound("origin"), world);
            }
            return new DistancedTarget(target, origin);
        }

        @Override
        public @NotNull NbtElement serialize(@NotNull DistancedTarget t) {
            NbtCompound cmp = new NbtCompound();
            cmp.put("target", Target.serialize(t.getTarget()));
            if (t.getOrigin() != null) {
                cmp.put("origin", Target.serialize(t.getOrigin()));
            }
            return cmp;
        }

        @Override
        public boolean exists(@NotNull DistancedTarget t) {
            return t.getTarget().exists();
        }

    };

    @NotNull
    Class<T> getValueClass();

    @NotNull
    DyeColor getColor();

    boolean exists(@NotNull T t);

    @NotNull
    T deserialize(@NotNull NbtElement nbt, ServerWorld world);

    @NotNull
    NbtElement serialize(@NotNull T t);

    @SuppressWarnings("unchecked")
    public static <T> NbtElement serialize(SpellType<T> type, Object t) {
        return type.serialize((T) t);
    }

}
