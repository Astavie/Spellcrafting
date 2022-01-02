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
        return new SpellType<?>[] { SpellType.TARGET };
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
            // TODO: Out of range particles
            return new Object[] { null };
        }

        if (t1.getTarget().asAttunable() == null || t2.getTarget().asAttunable() == null) {
            // TODO: Not attunable particles
            return new Object[] { null };
        }

        // Attune!
        t2.getTarget().asAttunable().attuneTo(t1.getTarget().asAttunable());

        // Return
        if (t2.getTarget().asAttunable().isAttunedTo(spell.getCaster().asAttunable())) {
            return new Object[] { new DistancedTarget(t2.getTarget(), t2.getTarget()) };
        }

        return new Object[] { null };
    }
    
}
