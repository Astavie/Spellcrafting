package astavie.spellcrafting.spell.node;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

// TODO: Impetus?
public class CharmMomentum implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[0];
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
            return new Object[0];
        }

        if (!(t1.getTarget() instanceof TargetEntity)) {
            // TODO: Not entity particles
            return new Object[0];
        }

        Entity e = ((TargetEntity) t1.getTarget()).getEntity();
        Vec3d dir = t2.getTarget().getPos().subtract(t1.getTarget().getPos()).normalize().multiply(2); // TODO: Variable speed

        System.out.println(ExceptionUtils.getStackTrace(new Throwable()));

        e.addVelocity(dir.x, dir.y, dir.z);
        ((ServerWorld) e.world).getChunkManager().sendToNearbyPlayers(e, new EntityVelocityUpdateS2CPacket(e));
        return new Object[0];
    }
    
}
