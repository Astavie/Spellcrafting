package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.SpellNode;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.util.Unit;

public class NodeStart implements SpellNode {

    @Override
    public @NotNull SpellType[] getParameters() {
        return new SpellType[0];
    }

    @Override
    public @NotNull SpellType[] getReturnTypes() {
        return new SpellType[] { SpellType.TIME, SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents() {
        return new ItemList(); // TODO: Initial components?
    }

    @Override
    public void apply(@NotNull Spell spell, boolean timeSent) {
        spell.apply(this, new Object[] {
            Unit.INSTANCE,
            new DistancedTarget(spell.getCaster().asTarget(), spell.getCaster().asTarget()),
            new DistancedTarget(spell.getTarget(),            spell.getCaster().asTarget()),
        });
    }
    
}
