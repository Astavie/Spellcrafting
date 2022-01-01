package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.SpellNode;
import astavie.spellcrafting.api.util.ItemList;

public class NodeEnd implements SpellNode {

    @Override
    public boolean applyOnChange() {
        return false;
    }

    @Override
    public @NotNull SpellType[] parameters() {
        return new SpellType[] { SpellType.TIME };
    }

    @Override
    public @NotNull SpellType[] returnTypes() {
        return new SpellType[0];
    }

    @Override
    public @NotNull ItemList components() {
        return new ItemList();
    }

    @Override
    public void apply(@NotNull Spell spell) {
        if (spell.getInput(this)[0] != null) {
            spell.schedule(this);
        }
    }

    @Override
    public void onEvent(@NotNull Spell spell, Spell.Event type, Object context) {
        spell.end();
    }
    
}
