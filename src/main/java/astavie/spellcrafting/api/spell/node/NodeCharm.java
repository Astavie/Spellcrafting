package astavie.spellcrafting.api.spell.node;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import net.minecraft.util.Unit;

public interface NodeCharm extends SpellNode {

    @Override
    default boolean applyOnChange() {
        return false;
    }

    @Override
    default @NotNull SpellType[] parameters() {
        return ArrayUtils.insert(0, charmParameters(), SpellType.TIME);
    }

    @Override
    default @NotNull SpellType[] returnTypes() {
        return ArrayUtils.insert(0, charmReturnTypes(), SpellType.TIME);
    }

    @Override
    default @NotNull Object[] apply(@NotNull Spell spell, @NotNull Object[] input) {
        if (input[0] == null) {
            return new Object[returnTypes().length];
        }

        return ArrayUtils.insert(0, cast(spell, Arrays.copyOfRange(input, 1, input.length)), Unit.INSTANCE);
    }

    @NotNull SpellType[] charmParameters();
    
    @NotNull SpellType[] charmReturnTypes();

    @NotNull Object[] cast(@NotNull Spell spell, @NotNull Object[] input);
    
}
