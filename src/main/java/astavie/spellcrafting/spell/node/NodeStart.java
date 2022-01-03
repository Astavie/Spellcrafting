package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.util.ItemList;

public class NodeStart implements NodeType {

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType[0];
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Initial components?
    }

    @Override
    public void apply(@NotNull Spell spell, @NotNull Spell.Node node) {
        spell.apply(node, new Object[] {
            new DistancedTarget(spell.getCaster(), spell.getCaster()),
            new DistancedTarget(spell.getTarget(), spell.getCaster()),
        });
    }
    
}
