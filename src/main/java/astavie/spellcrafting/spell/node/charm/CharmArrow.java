package astavie.spellcrafting.spell.node.charm;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity.PickupPermission;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class CharmArrow implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        if (node.getSize() == 1) {
            return new SpellType<?>[] { SpellType.TARGET };
        }
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList().addItem(Items.ARROW);
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget origin = (DistancedTarget) input[0];
        DistancedTarget target = node.node().getSize() == 1 ? null : (DistancedTarget) input[1];

        if (target == null) {
            if (!spell.existsAndInRange(origin)) {
                return new Object[1];
            }
        } else if (!spell.existsAndFirstInRange(origin, target, false)) {
            return new Object[1];
        }

        // Get target
        Vec3d t = target == null ? null : target.getTarget().getPos();
        if (t == null && origin.getTarget() instanceof TargetEntity) {
            Entity source = ((TargetEntity) origin.getTarget()).getEntity();
            t = source.getEyePos().add(source.getRotationVector());
        }

        if (t == null) {
            spell.onInvalidPosition(origin.getTarget().getWorld(), origin.getTarget().getPos());
            return new Object[1];
        }

        Vec3d dir = t.subtract(origin.getTarget().getPos());
        float f = 1.0f;

        // Create arrow entity
        ItemStack arrowItem = new ItemStack(Items.ARROW);
        ArrowEntity arrow = new ArrowEntity(origin.getTarget().getWorld(), origin.getTarget().getPos().x, origin.getTarget().getPos().y, origin.getTarget().getPos().z);
        if (origin.getCaster() instanceof TargetEntity) {
            // set caster entity as owner
            arrow.setOwner(((TargetEntity) origin.getCaster()).getEntity());
        }
        arrow.pickupType = PickupPermission.ALLOWED;
        arrow.initFromStack(arrowItem);
        arrow.setVelocity(dir.x, dir.y, dir.z, f * 3.0f, 1.0f);

        // Spawn arrow
        origin.getTarget().getWorld().spawnEntity(arrow);
        origin.getTarget().getWorld().playSound(null, origin.getTarget().getPos().x, origin.getTarget().getPos().y, origin.getTarget().getPos().z, SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (origin.getTarget().getWorld().getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);

        Target arrowTarget = new TargetEntity(arrow, origin.getTarget().getPos());
        return new Object[] { new DistancedTarget(arrowTarget, origin.getOrigin(), origin.getCaster()) };
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return recipe.size() == 1 && recipe.get(Items.ARROW) == 1 && sacrifice == null;
    }

}
