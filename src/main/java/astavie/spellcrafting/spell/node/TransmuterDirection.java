package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeTransmuter;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.util.math.Direction;

public class TransmuterDirection implements NodeTransmuter {

    private final Direction direction;

    public TransmuterDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?>[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];

        return new Object[] {
            new DistancedTarget(new TargetBlock(
                t1.getTarget().getWorld(),
                t1.getTarget().getBlock().offset(direction),
                t1.getTarget().getPos().add(direction.getOffsetX(), direction.getOffsetY(), direction.getOffsetZ()),
                direction
            ), t1.getOrigin(), t1.getCaster())
        };
    }
    
}
