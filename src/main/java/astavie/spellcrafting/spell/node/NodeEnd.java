package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.SpellNode;
import astavie.spellcrafting.api.util.ItemList;

public class NodeEnd implements SpellNode {

    @Override
    public @NotNull SpellType[] getParameters() {
        return new SpellType[] { SpellType.TIME };
    }

    @Override
    public @NotNull SpellType[] getReturnTypes() {
        return new SpellType[0];
    }

    @Override
    public @NotNull ItemList getComponents() {
        return new ItemList();
    }

    @Override
    public void apply(@NotNull Spell spell, boolean timeSent) {
        if (spell.getInput(this)[0] != null) {
            spell.schedule(this);
        }
    }

    @Override
    public void onEvent(@NotNull Spell spell, Spell.Event type, Object context) {
        spell.end();
    }
    
}
