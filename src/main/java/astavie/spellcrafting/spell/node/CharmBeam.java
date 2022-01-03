package astavie.spellcrafting.spell.node;

import java.util.Random;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.util.RaycastUtils;
import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class CharmBeam implements NodeCharm {

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
    @SuppressWarnings("resource")
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];
        DistancedTarget t2 = (DistancedTarget) input[1];

        if (!spell.inRange(t1)) {
            spell.onInvalidPosition(t1.getTarget().getPos());
            return new Object[] { null };
        }

        // Get target
        Entity source = null;
        if (t1.getTarget() instanceof TargetEntity) {
            source = ((TargetEntity) t1.getTarget()).getEntity();
        }

        Vec3d dir = t2.getTarget().getPos().subtract(t1.getTarget().getPos()).normalize();
        // TODO: Variable distance
        HitResult result = RaycastUtils.raycast(t1.getTarget().getWorld(), t1.getTarget().getPos(), t1.getTarget().getPos().add(dir.multiply(20)), source);

        // Spawn particles
        Random random = t1.getTarget().getWorld().random;
        double dis = result.getPos().distanceTo(t1.getTarget().getPos());
        for (double i = 1; i < dis; i += 0.333) {
            Vec3d pos = t1.getTarget().getPos().add(dir.multiply(i));
            ((ServerWorld) t1.getTarget().getWorld()).spawnParticles(ParticleTypes.ELECTRIC_SPARK, (double)pos.getX() + random.nextDouble() / 2 - 0.25, pos.getY() + random.nextDouble() / 2 - 0.25, (double)pos.getZ() + random.nextDouble() / 2 - 0.25, 1, 0.0, 0.0, 0.0, 0.2);
        }

        // Return target
        Target target = Spellcrafting.getTarget(t1.getTarget().getWorld(), result);
        spell.schedule(node);

        return new Object[] { new DistancedTarget(target, t1.getOrigin().withPos(target.getPos())) };
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Spell.Event type, @Nullable Object context) {
        // Reset after one tick
        spell.apply(node, new Object[] { null });
    }
    
}
