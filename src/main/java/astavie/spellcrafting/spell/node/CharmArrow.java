package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
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
    public @NotNull ItemList components() {
        ItemList list = new ItemList();
        list.addItem(Items.ARROW); // TODO: More components for speed?
        return list;
    }

    @Override
    public @NotNull SpellType[] charmParameters() {
        return new SpellType[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType[] charmReturnTypes() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Object[] input) {
        Target origin = (Target) input[0];
        Target target = (Target) input[1];

        if (origin.getWorld() != target.getWorld() || !spell.inRange(origin)) {
            // TODO: Out of range particles
            return new Object[] { null };
        }

        Vec3d dir = target.getPos().subtract(origin.getPos());
        float f = 1.0f;

        // Create arrow entity
        ItemStack arrowItem = new ItemStack(Items.ARROW);
        ArrowEntity arrow = new ArrowEntity(origin.getWorld(), origin.getPos().x, origin.getPos().y, origin.getPos().z);
        arrow.setOwner(spell.caster().asTarget().getEntity()); // set caster entity as owner
        arrow.pickupType = PickupPermission.ALLOWED;
        arrow.initFromStack(arrowItem);
        arrow.setVelocity(dir.x, dir.y, dir.z, f * 3.0f, 1.0f);
        if (f == 1.0f) {
            arrow.setCritical(true);
        }

        // Spawn arrow
        origin.getWorld().spawnEntity(arrow);
        origin.getWorld().playSound(null, origin.getPos().x, origin.getPos().y, origin.getPos().z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (origin.getWorld().getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);

        return new Object[] { new TargetEntity(arrow, origin.getPos(), origin.getOrigin()) };
    }
    
}
