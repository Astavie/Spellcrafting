package astavie.spellcrafting.spell.node.charm;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CharmSummon implements NodeCharm {

    private final EntityType<?> type;
    private final ItemList components;

    public CharmSummon(EntityType<?> type, ItemList components) {
        this.type = type;
        this.components = components;
    }

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType<?> [] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?> [] { SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return components;
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget t1 = (DistancedTarget) input[0];

        if (!spell.existsAndInRange(t1)) {
            return new Object[1];
        }

        BlockPos pos = t1.getTarget().getBlock();
        if (!t1.getTarget().getWorld().isAir(pos) && t1.getTarget() instanceof TargetBlock) {
            Direction dir = ((TargetBlock) t1.getTarget()).getSide();
            if (dir != null) pos = pos.offset(dir);
        }

        // TODO: Particles

        Entity e = type.spawn((ServerWorld) t1.getTarget().getWorld(), null, null, null, pos, SpawnReason.MOB_SUMMONED, false, false);
        if (e == null) {
            return new Object[] { null };
        }

        Target target = new TargetEntity(e, t1.getTarget().getPos());

        // Attune if attunable
        Attunable attunable = Attunable.ENTITY_ATTUNABLE.find(e, null);
        Attunable caster = t1.getCaster().asAttunable();
        if (attunable != null && caster != null) {
            attunable.attuneTo(caster);

            return new Object[] { new DistancedTarget(target, target, t1.getCaster()) };
        }

        return new Object[] { new DistancedTarget(target, t1.getOrigin(), t1.getCaster()) };
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return recipe.size() == 1 && recipe.get(Items.EGG) == 1 && sacrifice == type;
    }
    
}
