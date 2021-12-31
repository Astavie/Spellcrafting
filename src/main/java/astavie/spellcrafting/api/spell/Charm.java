package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.util.ItemList;

public interface Charm extends SpellComponent {

    @NotNull Class<?>[] getArgumentTypes();

    default boolean isBlocking() {
        return false;
    }

    @NotNull ItemList getComponents();
    
    void cast(@NotNull ActiveSpell context, @NotNull Object[] arguments, int id);
    
}
