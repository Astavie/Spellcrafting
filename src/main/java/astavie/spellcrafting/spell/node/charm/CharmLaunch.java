package astavie.spellcrafting.spell.node.charm;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

// TODO: Impetus?
public class CharmLaunch implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[0];
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList().addItem(Items.GUNPOWDER);
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];
        DistancedTarget t2 = (DistancedTarget) input[1];

        if (!spell.existsAndFirstInRange(t1, t2, false)) {
            return new Object[0];
        }

        if (!(t1.getTarget() instanceof TargetEntity)) {
            spell.onInvalidPosition(t1.getTarget().getWorld(), t1.getTarget().getPos());
            return new Object[0];
        }

        // TODO: Particles

        Entity e = ((TargetEntity) t1.getTarget()).getEntity();

        // Only allow on ground or low velocity
        if (!e.isOnGround() && e.getVelocity().lengthSquared() > 0.1) {
            spell.onInvalidPosition(t1.getTarget().getWorld(), t1.getTarget().getPos());
            return new Object[0];
        }

        Vec3d dir = t2.getTarget().getPos().subtract(t1.getTarget().getPos()).normalize().multiply(2); // TODO: Variable speed

        e.addVelocity(dir.x, dir.y, dir.z);
        ((ServerWorld) e.world).getChunkManager().sendToNearbyPlayers(e, new EntityVelocityUpdateS2CPacket(e));
        return new Object[0];
    }
    
}
