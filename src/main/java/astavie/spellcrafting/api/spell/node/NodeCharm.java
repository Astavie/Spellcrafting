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
    default void apply(@NotNull Spell spell) {
        Object[] input = spell.getInput(this);
        if (input[0] != null) {
            spell.apply(this, ArrayUtils.insert(0, cast(spell, Arrays.copyOfRange(input, 1, input.length)), (Unit) null));
            spell.schedule(this);
        }
    }

    @Override
    default void onEvent(@NotNull Spell spell, Spell.Event type, Object context) {
        spell.apply(this, 0, Unit.INSTANCE);
    }

    @NotNull SpellType[] charmParameters();
    
    @NotNull SpellType[] charmReturnTypes();

    @NotNull Object[] cast(@NotNull Spell spell, @NotNull Object[] input);
    
}
