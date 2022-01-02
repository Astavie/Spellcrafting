package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;

public interface SpellType<T> {

    public static final @NotNull SpellType<Unit> TIME = new SpellType<Unit>() {

        @Override
        public @NotNull Class<Unit> getValueClass() {
            return Unit.class;
        }

        @Override
        public @NotNull DyeColor getColor() {
            return DyeColor.WHITE;
        }

        @Override
        public @NotNull Unit deserialize(@NotNull NbtElement nbt, ServerWorld world) {
            return Unit.INSTANCE;
        }

        @Override
        public @NotNull NbtElement serialize(@NotNull Unit unit) {
            return NbtByte.ONE;
        }

        @Override
        public boolean exists(Unit t) {
            return true;
        }
        
    };
    public static final @NotNull SpellType<DistancedTarget> TARGET = new SpellType<DistancedTarget>() {

        @Override
        public @NotNull Class<DistancedTarget> getValueClass() {
            return DistancedTarget.class;
        }

        @Override
        public @NotNull DyeColor getColor() {
            return DyeColor.GREEN;
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
        public boolean exists(DistancedTarget t) {
            return t.getTarget().exists();
        }

    };

    @NotNull
    Class<T> getValueClass();

    @NotNull
    DyeColor getColor();

    boolean exists(T t);

    @NotNull
    T deserialize(@NotNull NbtElement nbt, ServerWorld world);

    @NotNull
    NbtElement serialize(@NotNull T t);

    @SuppressWarnings("unchecked")
    public static <T> NbtElement serialize(SpellType<T> type, Object t) {
        return type.serialize((T) t);
    }

}
