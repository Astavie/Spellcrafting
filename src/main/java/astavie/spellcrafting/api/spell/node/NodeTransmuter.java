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
    default @NotNull Object[] apply(@NotNull Spell spell, @NotNull Object[] input) {
        if (ArrayUtils.contains(input, null)) {
            return new Object[] { returnTypes().length };
        }
        return transmute(spell, input);
    }

    @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Object[] input);
    
}
