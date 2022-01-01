package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeEvent;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;

public class EventTarget implements NodeEvent<Target> {

    @Override
    public @NotNull ItemList components() {
        return new ItemList();
    }

    @Override
    public Spell.Event<Target> getEvent(@NotNull Spell spell, @NotNull Object[] input) {
        return Spell.Event.TARGET;
    }

    @Override
    public @NotNull SpellType[] eventParameters() {
        return new SpellType[0];
    }

    @Override
    public @NotNull SpellType[] eventReturnTypes() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Object[] input, Target context) {
        return new Object[] { context };
    }
    
}
