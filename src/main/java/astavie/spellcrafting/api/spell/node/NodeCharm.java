package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeCharm extends NodeType {

    @Override
    default void apply(@NotNull Spell spell, @NotNull Spell.Node node) {
        Object[] input = spell.getInput(node);
        if (ArrayUtils.contains(input, null)) {
            spell.apply(node, new Object[getReturnTypes(spell, node).length]);
            return;
        }
        spell.apply(node, cast(spell, node, input));
    }

    @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Object[] input);
    
}
