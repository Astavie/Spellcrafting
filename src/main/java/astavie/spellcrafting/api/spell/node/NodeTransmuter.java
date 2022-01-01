package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeTransmuter extends SpellNode {

    @Override
    default void apply(@NotNull Spell spell, boolean timeSent) {
        Object[] input = spell.getInput(this);
        if (ArrayUtils.contains(input, null)) {
            spell.apply(this, new Object[] { getReturnTypes().length });
        }
        spell.apply(this, transmute(spell, input));
    }

    @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Object[] input);
    
}
