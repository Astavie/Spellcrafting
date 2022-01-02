package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeTransmuter extends NodeType {

    @Override
    default void apply(@NotNull Spell spell, @NotNull Spell.Node node, boolean timeSent) {
        Object[] input = spell.getInput(node);
        if (ArrayUtils.contains(input, null)) {
            spell.apply(node, new Object[getReturnTypes().length]);
        }
        spell.apply(node, transmute(spell, input));
    }

    @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Object[] input);
    
}
