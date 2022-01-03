package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.spell.target.TargetRaycast;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.spell.util.RaycastUtils;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class CharmRaycast implements NodeCharm {

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

        if (!spell.inRange(t1)) {
            // TODO: Out of range particles
            return new Object[] { null };
        }

        Entity source = null;
        if (t1.getTarget() instanceof TargetEntity) {
            source = ((TargetEntity) t1.getTarget()).getEntity();
        }

        Vec3d dir = t2.getTarget().getPos().subtract(t1.getTarget().getPos()).normalize().multiply(20); // TODO: Variable distance
        HitResult result = RaycastUtils.raycast(t1.getTarget().getWorld(), t1.getTarget().getPos(), t1.getTarget().getPos().add(dir), source);

        Target target = Spellcrafting.getTarget(t1.getTarget().getWorld(), result);

        return new Object[] { new DistancedTarget(target, new TargetRaycast(target.getWorld(), target.getPos())) };
    }
    
}
