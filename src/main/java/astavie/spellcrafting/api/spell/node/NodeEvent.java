package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeEvent<T> extends NodeType {

    @Override
    default void apply(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        Object[] input = spell.getInput(node);
        if (!ArrayUtils.contains(input, null)) {
            Spell.Event event = getEvent(spell, node, input);
            if (event != null) {
                spell.registerEvent(event, node);
            } else {
                spell.cancelEvents(node);
            }
        } else {
            spell.cancelEvents(node);
        }

        spell.apply(node, new Object[getReturnTypes(spell, node.node()).length]);
    }

    @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input);

    @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @Nullable T context);

    @Override
    @SuppressWarnings("unchecked")
    default void onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Spell.Event type, @Nullable Object context) {
        Object[] ret = onEvent(spell, node, (T) context);
        spell.apply(node, ret);
    };
    
}
