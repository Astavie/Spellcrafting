package astavie.spellcrafting.api.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.util.ItemList;

public interface SpellNode {

    @NotNull SpellType[] getParameters();

    @NotNull SpellType[] getReturnTypes();

    @NotNull ItemList getComponents();

    default int getComponentFactor(int ouputIndex) {
        return 1;
    }

    void apply(@NotNull Spell spell, boolean timeSent);

    default void onEvent(@NotNull Spell spell, Spell.Event type, Object context) {
    }
    
}
