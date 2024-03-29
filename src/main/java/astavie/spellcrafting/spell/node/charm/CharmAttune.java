package astavie.spellcrafting.spell.node.charm;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

public class CharmAttune implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];
        DistancedTarget t2 = (DistancedTarget) input[1];

        if (!spell.existsAndInRange(t1, t2)) {
            return new Object[2];
        }

        if (t1.getTarget().asAttunable() == null || t2.getTarget().asAttunable() == null) {
            spell.onInvalidPosition(t1.getTarget().getWorld(), t1.getTarget().getPos());
            spell.onInvalidPosition(t2.getTarget().getWorld(), t2.getTarget().getPos());
            return new Object[2];
        }

        // TODO: Particles

        // Attune!
        t2.getTarget().asAttunable().attuneTo(t1.getTarget().asAttunable());

        // Return
        return new Object[] {
            new DistancedTarget(t1.getTarget(), t1.getTarget(), t1.getCaster()),
            new DistancedTarget(t2.getTarget(), t2.getTarget(), t2.getCaster())
        };
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return recipe.size() == 1 && recipe.get(Items.BELL) == 1 && sacrifice == null;
    }
    
}
