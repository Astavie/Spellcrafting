package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class CharmExplode implements NodeCharm {

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        ItemList list = new ItemList();
        list.addItem(Items.GUNPOWDER); // TODO: More components for bigger explosio?
        return list;
    }

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType[0];
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget origin = (DistancedTarget) input[0];

        if (!origin.inRange()) {
            spell.onInvalidPosition(origin.getTarget().getWorld(), origin.getTarget().getPos());
            return new Object[0];
        }

        Target target = origin.getTarget();
        Vec3d pos = target.getPos();

        Entity source = null;
        if (target instanceof TargetEntity) {
            source = ((TargetEntity) target).getEntity();
        }
        target.getWorld().createExplosion(source, pos.x, pos.y, pos.z, 4.0f, Explosion.DestructionType.BREAK);

        return new Object[0];
    }
    
}
