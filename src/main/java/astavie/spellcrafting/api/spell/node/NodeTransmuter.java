package astavie.spellcrafting.api.spell.node;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeTransmuter extends SpellNode {

    @Override
    default boolean applyOnChange() {
        return true;
    }

    @Override
    default void apply(@NotNull Spell spell) {
        Object[] input = spell.getInput(this);
        if (ArrayUtils.contains(input, null)) {
            spell.apply(this, new Object[] { returnTypes().length });
        }
        spell.apply(this, transmute(spell, input));
    }

    @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Object[] input);
    
}
