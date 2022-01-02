package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class CharmArrow implements NodeCharm {

    @Override
    public @NotNull ItemList getComponents() {
        ItemList list = new ItemList();
        list.addItem(Items.ARROW); // TODO: More components for speed?
        return list;
    }

    @Override
    public @NotNull SpellType<?>[] getCharmParameters() {
        return new SpellType[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getCharmReturnTypes() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Object[] input) {
        DistancedTarget origin = (DistancedTarget) input[0];
        DistancedTarget target = (DistancedTarget) input[1];

        if (origin.getTarget().getWorld() != target.getTarget().getWorld() || !spell.inRange(origin)) {
            // TODO: Out of range particles
            return new Object[] { null };
        }

        Vec3d dir = target.getTarget().getPos().subtract(origin.getTarget().getPos());
        float f = 1.0f;

        // Create arrow entity
        ItemStack arrowItem = new ItemStack(Items.ARROW);
        ArrowEntity arrow = new ArrowEntity(origin.getTarget().getWorld(), origin.getTarget().getPos().x, origin.getTarget().getPos().y, origin.getTarget().getPos().z);
        if (spell.getCaster().asTarget() instanceof TargetEntity) {
            arrow.setOwner(((TargetEntity) spell.getCaster().asTarget()).getEntity()); // set caster entity as owner
        }
        arrow.pickupType = PickupPermission.ALLOWED;
        arrow.initFromStack(arrowItem);
        arrow.setVelocity(dir.x, dir.y, dir.z, f * 3.0f, 1.0f);
        if (f == 1.0f) {
            arrow.setCritical(true);
        }

        // Attune arrow
        Attunable.ENTITY_ATTUNABLE.find(arrow, null).attuneTo(spell.getCaster().asAttunable());

        // Spawn arrow
        origin.getTarget().getWorld().spawnEntity(arrow);
        origin.getTarget().getWorld().playSound(null, origin.getTarget().getPos().x, origin.getTarget().getPos().y, origin.getTarget().getPos().z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (origin.getTarget().getWorld().getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);

        Target arrowTarget = new TargetEntity(arrow, origin.getTarget().getPos());
        return new Object[] { new DistancedTarget(arrowTarget, arrowTarget) };
    }
    
}
