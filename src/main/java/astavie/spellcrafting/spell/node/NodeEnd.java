package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.util.ItemList;

public class NodeEnd implements NodeType {

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType[] { SpellType.TIME };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes() {
        return new SpellType[0];
    }

    @Override
    public @NotNull ItemList getComponents() {
        return new ItemList();
    }

    @Override
    public void apply(@NotNull Spell spell, @NotNull Spell.Node node, boolean timeSent) {
        if (spell.getInput(node)[0] != null) {
            spell.schedule(node);
        }
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.Node node, Spell.Event type, Object context) {
        spell.end();
    }
    
}
