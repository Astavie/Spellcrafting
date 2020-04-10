package astavie.spellcrafting.api.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

/**
 * Items that implement this interface will play the casting animation while held
 */
public interface ISpellItem {

	/**
	 * @return if the player should play the casting animation while the item is held
	 */
	default boolean useCastingAnimation(World world, PlayerEntity player, Hand hand, ItemStack stack, int ticks) {
		return true;
	}

}
