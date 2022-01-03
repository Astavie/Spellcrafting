package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

public class CharmIgnite implements NodeCharm {

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components
    }

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType[0];
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        DistancedTarget d = (DistancedTarget) input[0];

        if (!d.inRange()) {
            spell.onInvalidPosition(d.getTarget().getWorld(), d.getTarget().getPos());
            return new Object[0];
        }

        Target target = d.getTarget();

        // Entity
        if (target instanceof TargetEntity) {
            Entity entity = ((TargetEntity) target).getEntity();
            if (!entity.isFireImmune()) {
                target.getWorld().playSoundFromEntity(null, entity, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (target.getWorld().getRandom().nextFloat() - target.getWorld().getRandom().nextFloat()) * 0.2f + 1.0f);
                entity.setOnFireFor(5);
            }
            return new Object[0];
        }

        // Special block
        BlockState blockState = target.getWorld().getBlockState(target.getBlock());
        if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
            target.getWorld().playSound(null, target.getBlock(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (target.getWorld().getRandom().nextFloat() - target.getWorld().getRandom().nextFloat()) * 0.2f + 1.0f);
            target.getWorld().setBlockState(target.getBlock(), (BlockState)blockState.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            target.getWorld().emitGameEvent(null, GameEvent.BLOCK_PLACE, target.getBlock());
            return new Object[0];
        }

        // Fire
        BlockPos blockPos2 = target.getBlock();
        if (!target.getWorld().isAir(target.getBlock()) && target instanceof TargetBlock) {
            blockPos2 = target.getBlock().offset(((TargetBlock) target).getSide());
        }

        if (AbstractFireBlock.canPlaceAt(target.getWorld(), blockPos2, Direction.NORTH)) {
            target.getWorld().playSound(null, blockPos2, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (target.getWorld().getRandom().nextFloat() - target.getWorld().getRandom().nextFloat()) * 0.2f + 1.0f);
            BlockState blockState2 = AbstractFireBlock.getState(target.getWorld(), blockPos2);
            target.getWorld().setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            target.getWorld().emitGameEvent(null, GameEvent.BLOCK_PLACE, target.getBlock());
            return new Object[0];
        }
        return new Object[0];
    }
    
}
