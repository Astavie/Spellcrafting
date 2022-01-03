package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.util.ItemList;

public class CharmAttune implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];
        DistancedTarget t2 = (DistancedTarget) input[1];

        if (!spell.inRange(t1) || !spell.inRange(t2)) {
            spell.onInvalidPosition(t1.getTarget().getPos());
            spell.onInvalidPosition(t2.getTarget().getPos());
            return new Object[] { null, null };
        }

        if (t1.getTarget().asAttunable() == null || t2.getTarget().asAttunable() == null) {
            spell.onInvalidPosition(t1.getTarget().getPos());
            spell.onInvalidPosition(t2.getTarget().getPos());
            return new Object[] { null, null };
        }

        // Attune!
        t2.getTarget().asAttunable().attuneTo(t1.getTarget().asAttunable());

        // Return
        return new Object[] {
            new DistancedTarget(t1.getTarget(), t1.getTarget()),
            new DistancedTarget(t2.getTarget(), t2.getTarget())
        };
    }
    
}
