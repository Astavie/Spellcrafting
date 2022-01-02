package astavie.spellcrafting.api.spell.node;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import net.minecraft.util.Unit;

public interface NodeEvent<T> extends NodeType {

    @Override
    default @NotNull SpellType<?>[] getParameters() {
        return ArrayUtils.insert(0, getEventParameters(), SpellType.TIME);
    }

    @Override
    default @NotNull SpellType<?>[] getReturnTypes() {
        return ArrayUtils.insert(0, getEventReturnTypes(), SpellType.TIME);
    }

    @Override
    default void apply(@NotNull Spell spell, @NotNull Spell.Node node, boolean timeSent) {
        if (timeSent) {
            Object[] input = spell.getInput(node);
            if (input[0] != null) {
                Spell.Event event = getEvent(spell, Arrays.copyOfRange(input, 1, input.length));
                if (event != null) {
                    spell.registerEvent(event, node);
                    return;
                }
            }
        }

        spell.cancelEvents(node);
    }

    @NotNull SpellType<?>[] getEventParameters();
    
    @NotNull SpellType<?>[] getEventReturnTypes();

    @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Object[] input);

    @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Spell.Node node, T context);

    @Override
    @SuppressWarnings("unchecked")
    default void onEvent(Spell spell, @NotNull Spell.Node node, Spell.Event type, Object context) {
        Object[] ret = onEvent(spell, node, (T) context);
        spell.apply(node, ArrayUtils.insert(0, ret, Unit.INSTANCE));
    };
    
}
