package astavie.spellcrafting.api.spell.node;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import net.minecraft.util.Unit;

public interface NodeEvent<T> extends SpellNode {

    @Override
    default boolean applyOnChange() {
        return false;
    }

    @Override
    default @NotNull SpellType[] parameters() {
        return ArrayUtils.insert(0, eventParameters(), SpellType.TIME);
    }

    @Override
    default @NotNull SpellType[] returnTypes() {
        return ArrayUtils.insert(0, eventReturnTypes(), SpellType.TIME);
    }

    @Override
    default @NotNull Object[] apply(@NotNull Spell spell, @NotNull Object[] input) {
        if (input[0] != null) {
            Spell.Event<?> event = getEvent(spell, Arrays.copyOfRange(input, 1, input.length));
            if (event != null) {
                spell.registerEvent(event, this);
            }
        }

        return new Object[returnTypes().length];
    }

    @NotNull SpellType[] eventParameters();
    
    @NotNull SpellType[] eventReturnTypes();

    @Nullable Spell.Event<?> getEvent(@NotNull Spell spell, @NotNull Object[] input);

    @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Object[] input, T context);

    @SuppressWarnings("unchecked")
    default <U> void onEvent(Spell spell, Object[] input, Spell.Event<U> type, U context) {
        Object[] ret = onEvent(spell, Arrays.copyOfRange(input, 1, input.length), (T) context);
        spell.apply(this, ArrayUtils.insert(0, ret, Unit.INSTANCE));
    };
    
}
