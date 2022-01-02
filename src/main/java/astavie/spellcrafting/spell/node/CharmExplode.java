package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;

public class CharmExplode implements NodeCharm {

    @Override
    public @NotNull ItemList getComponents() {
        ItemList list = new ItemList();
        list.addItem(Items.GUNPOWDER); // TODO: More components for bigger explosio?
        return list;
    }

    @Override
    public @NotNull SpellType<?>[] getCharmParameters() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getCharmReturnTypes() {
        return new SpellType[0];
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Object[] input) {
        DistancedTarget origin = (DistancedTarget) input[0];

        if (!spell.inRange(origin)) {
            // TODO: Out of range particles
            return new Object[0];
        }

        Target target = origin.getTarget();
        Vec3d pos = target.getPos();

        // TODO: Explosion.getCausingEntity -> Caster ?
        target.getWorld().createExplosion(null, pos.x, pos.y, pos.z, 4.0f, Explosion.DestructionType.BREAK);

        return new Object[0];
    }
    
}