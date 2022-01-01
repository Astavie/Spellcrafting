package astavie.spellcrafting.api.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.util.ItemList;

public interface SpellNode {

    boolean applyOnChange();

    @NotNull SpellType[] parameters();

    @NotNull SpellType[] returnTypes();

    @NotNull ItemList components();

    default int componentFactor(int ouputIndex) {
        return 1;
    }

    void apply(@NotNull Spell spell);

    default void onEvent(@NotNull Spell spell, Spell.Event type, Object context) {
    }
    
}
