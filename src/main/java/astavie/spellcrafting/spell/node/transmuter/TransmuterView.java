package astavie.spellcrafting.spell.node.transmuter;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeTransmuter;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TransmuterView implements NodeTransmuter {

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList();
    }

    @Override
    public @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget target = (DistancedTarget) input[0];

        if (target == null || !target.getTarget().exists() || !(target.getTarget() instanceof TargetEntity)) {
            return new Object[1];
        }

        Entity e = ((TargetEntity) target.getTarget()).getEntity();

        Vec3d pos = e.getEyePos().add(e.getRotationVector());
        Target out = new TargetBlock((ServerWorld) e.world, new BlockPos(pos), pos, null);

        return new Object[] { new DistancedTarget(out, target.getOrigin(), target.getCaster()) };
    }

    @Override
    public boolean matches(int size, ItemList recipe) {
        return recipe.size() == 1 && recipe.get(Items.SPIDER_EYE) == 1;
    }
    
}
