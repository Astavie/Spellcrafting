package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeEvent;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;

public class EventTarget implements NodeEvent<Target> {

    @Override
    public @NotNull ItemList getComponents() {
        return new ItemList();
    }

    @Override
    public Spell.Event getEvent(@NotNull Spell spell, @NotNull Object[] input) {
        return Spell.Event.TARGET;
    }

    @Override
    public @NotNull SpellType[] getEventParameters() {
        return new SpellType[0];
    }

    @Override
    public @NotNull SpellType[] getEventReturnTypes() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, Target context) {
        return new Object[] { new DistancedTarget(context, spell.getCaster().asTarget()) };
    }
    
}
