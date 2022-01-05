package astavie.spellcrafting.item;

import astavie.spellcrafting.block.BlockMagicLine;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;

public class BlockItemMagicLine extends BlockItem {

    public BlockItemMagicLine(BlockMagicLine block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockState block = context.getWorld().getBlockState(context.getBlockPos());
        if (block.getBlock() != getBlock()) {
            return super.useOnBlock(context);
        }

        ActionResult result = ((BlockMagicLine) getBlock()).onChalk(context.getWorld(), context.getBlockPos(), block, context.getPlayerFacing());
        
        if (result.isAccepted()) {
            BlockSoundGroup blockSoundGroup = block.getSoundGroup();
            context.getWorld().playSound(context.getPlayer(), context.getBlockPos(), getPlaceSound(block), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0f) / 2.0f, blockSoundGroup.getPitch() * 0.8f);
            if (context.getPlayer() == null || !context.getPlayer().getAbilities().creativeMode) {
                context.getStack().decrement(1);
            }
        }

        return result;
    }
    
}
