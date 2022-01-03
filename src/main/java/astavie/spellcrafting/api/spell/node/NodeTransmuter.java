package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeTransmuter extends NodeType {

    @Override
    default void apply(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        Object[] input = spell.getInput(node);
        if (ArrayUtils.contains(input, null)) {
            spell.apply(node, new Object[getReturnTypes(spell, node.node()).length]);
            return;
        }
        spell.apply(node, transmute(spell, node, input));
    }

    @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input);
    
}
