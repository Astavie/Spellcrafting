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
    default @NotNull SpellType[] getParameters() {
        return ArrayUtils.insert(0, getEventParameters(), SpellType.TIME);
    }

    @Override
    default @NotNull SpellType[] getReturnTypes() {
        return ArrayUtils.insert(0, getEventReturnTypes(), SpellType.TIME);
    }

    @Override
    default void apply(@NotNull Spell spell, boolean timeSent) {
        if (timeSent) {
            Object[] input = spell.getInput(this);
            if (input[0] != null) {
                Spell.Event event = getEvent(spell, Arrays.copyOfRange(input, 1, input.length));
                if (event != null) {
                    spell.registerEvent(event, this);
                    return;
                }
            }
        }

        spell.cancelEvents(this);
    }

    @NotNull SpellType[] getEventParameters();
    
    @NotNull SpellType[] getEventReturnTypes();

    @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Object[] input);

    @NotNull Object[] onEvent(@NotNull Spell spell, T context);

    @SuppressWarnings("unchecked")
    default void onEvent(Spell spell, Spell.Event type, Object context) {
        Object[] ret = onEvent(spell, (T) context);
        spell.apply(this, ArrayUtils.insert(0, ret, Unit.INSTANCE));
    };
    
}
