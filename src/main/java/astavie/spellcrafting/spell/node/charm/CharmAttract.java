package astavie.spellcrafting.spell.node.charm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class CharmAttract implements NodeCharm {

    // TODO: Variable time
    // TODO: Variable range
    // TODO: Variable speed

    private static final int TICKS = 40;
    private static final double RANGE = 10;

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
        return new ItemList().addItem(Items.FEATHER); // TODO: ???
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        spell.scheduleFor(node, TICKS);
        return new Object[0];
    }

    private void effect(Entity e, Vec3d target) {
        double existingVel = e.getVelocity().length();

        Vec3d dir = target.subtract(e.getPos()).normalize().multiply(Math.max(0.2 - existingVel, 0));

        e.addVelocity(dir.x, dir.y, dir.z);
        ((ServerWorld) e.world).getChunkManager().sendToNearbyPlayers(e, new EntityVelocityUpdateS2CPacket(e));
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Spell.Event type, @Nullable Object context) {
        Object[] input = spell.getInput(node);
        
        DistancedTarget t1 = (DistancedTarget) input[0];
        DistancedTarget t2 = (DistancedTarget) input[1];

        if (t1 == null || t2 == null) {
            spell.cancelEvents(node);
            return;
        }

        if (!spell.existsAndFirstInRange(t1, t2, true)) {
            return;
        }

        if (
            !(t2.getTarget() instanceof TargetEntity) ||
            t2.getTarget().getPos().squaredDistanceTo(t1.getTarget().getPos()) > RANGE * RANGE
        ) {
            spell.onInvalidPosition(t1.getTarget().getWorld(), t1.getTarget().getPos());
            spell.onInvalidPosition(t2.getTarget().getWorld(), t2.getTarget().getPos());
            spell.cancelEvents(node);
            return;
        }

        effect(((TargetEntity) t2.getTarget()).getEntity(), t1.getTarget().getPos());
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return false; // TODO: Recipe
    }
    
}
