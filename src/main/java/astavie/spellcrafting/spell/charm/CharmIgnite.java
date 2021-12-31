package astavie.spellcrafting.spell.charm;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.ActiveSpell;
import astavie.spellcrafting.api.spell.Charm;
import astavie.spellcrafting.api.spell.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.event.GameEvent;

public class CharmIgnite implements Charm {

    @Override
    public Identifier getIdentifier() {
        return new Identifier("spellcrafting:ignite");
    }

    @Override
    public @NotNull Class<?>[] getArgumentTypes() {
        return new Class<?>[] {
            Target.class
        };
    }

    @Override
    public @NotNull ItemList getComponents() {
        return new ItemList(); // TODO: Components
    }

    @Override
    public void cast(@NotNull ActiveSpell context, @NotNull Object[] arguments, int id) {
        Target target = (Target) arguments[0];

        if (target == null || !context.inRange(target)) {
            // TODO: "Too far away" particle effect
            return;
        }

        // Entity
        if (target.getEntity() != null) {
            if (!target.getEntity().isFireImmune()) {
                target.getWorld().playSoundFromEntity(null, target.getEntity(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (target.getWorld().getRandom().nextFloat() - target.getWorld().getRandom().nextFloat()) * 0.2f + 1.0f);
                target.getEntity().setOnFireFor(5);
            }
            return;
        }

        // Special block
        BlockState blockState = target.getWorld().getBlockState(target.getBlock());
        if (CampfireBlock.canBeLit(blockState) || CandleBlock.canBeLit(blockState) || CandleCakeBlock.canBeLit(blockState)) {
            target.getWorld().playSound(null, target.getBlock(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (target.getWorld().getRandom().nextFloat() - target.getWorld().getRandom().nextFloat()) * 0.2f + 1.0f);
            target.getWorld().setBlockState(target.getBlock(), (BlockState)blockState.with(Properties.LIT, true), Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            target.getWorld().emitGameEvent(null, GameEvent.BLOCK_PLACE, target.getBlock());
            return;
        }

        // Fire
        BlockPos blockPos2;
        if (target.getWorld().isAir(target.getBlock())) {
            blockPos2 = target.getBlock();
        } else {
            blockPos2 = target.getBlock().offset(target.getDirection());
        }

        if (AbstractFireBlock.canPlaceAt(target.getWorld(), blockPos2, Direction.NORTH)) {
            target.getWorld().playSound(null, blockPos2, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0f, (target.getWorld().getRandom().nextFloat() - target.getWorld().getRandom().nextFloat()) * 0.2f + 1.0f);
            BlockState blockState2 = AbstractFireBlock.getState(target.getWorld(), blockPos2);
            target.getWorld().setBlockState(blockPos2, blockState2, Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);
            target.getWorld().emitGameEvent(null, GameEvent.BLOCK_PLACE, target.getBlock());
            return;
        }
        return;
    }
    
}
