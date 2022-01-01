package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.SpellNode;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.util.Unit;

public class NodeStart implements SpellNode {

    @Override
    public boolean applyOnChange() {
        return false;
    }

    @Override
    public @NotNull SpellType[] parameters() {
        return new SpellType[0];
    }

    @Override
    public @NotNull SpellType[] returnTypes() {
        return new SpellType[] { SpellType.TIME, SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList components() {
        return new ItemList(); // TODO: Initial components?
    }

    @Override
    public @NotNull Object[] apply(@NotNull Spell spell, @NotNull Object[] input) {
        return new Object[] { Unit.INSTANCE, spell.caster().asTarget(), spell.target() };
    }
    
}
