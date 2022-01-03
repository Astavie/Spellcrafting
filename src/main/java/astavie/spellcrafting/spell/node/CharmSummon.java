package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class CharmSummon implements NodeCharm {

    private final EntityType<?> type;

    public CharmSummon(EntityType<?> type) {
        this.type = type;
    }

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?> [] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?> [] { SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];

        if (!t1.inRange()) {
            spell.onInvalidPosition(t1.getTarget().getWorld(), t1.getTarget().getPos());
            return new Object[] { null };
        }

        BlockPos pos = t1.getTarget().getBlock();
        if (!t1.getTarget().getWorld().isAir(pos) && t1.getTarget() instanceof TargetBlock) {
            pos = pos.offset(((TargetBlock) t1.getTarget()).getSide());
        }

        Entity e = type.spawn((ServerWorld) t1.getTarget().getWorld(), null, null, null, pos, SpawnReason.MOB_SUMMONED, false, false);
        
        return new Object[] { e == null ? null : new DistancedTarget(new TargetEntity(e, t1.getTarget().getPos()), t1.getOrigin(), t1.getCaster()) };
    }
    
}
