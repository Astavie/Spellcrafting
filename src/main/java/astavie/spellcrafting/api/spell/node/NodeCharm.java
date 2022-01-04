package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeCharm extends NodeType {

    @Override
    default void onOn(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        Object[] input = spell.getInput(node);
        if (ArrayUtils.contains(input, null)) {
            spell.cancelEvents(node);
            spell.apply(node, new Object[getReturnTypes(spell, node.node()).length]);
            return;
        }
        spell.apply(node, cast(spell, node, input));
    }

    @Override
    default void onOff(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        spell.cancelEvents(node);
        spell.apply(node, new Object[getReturnTypes(spell, node.node()).length]);
    }

    @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input);
    
}
