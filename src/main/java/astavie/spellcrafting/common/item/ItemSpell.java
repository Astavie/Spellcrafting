package astavie.spellcrafting.common.item;

import astavie.spellcrafting.api.item.ISpellItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public abstract class ItemSpell extends Item implements ISpellItem {

	public ItemSpell(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (canCastSpell(world, player, hand, stack)) {
			// Cast spell
			player.setActiveHand(hand);
			return ActionResult.resultPass(stack);
		} else {
			return ActionResult.resultFail(stack);
		}
	}

	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	@Override
	public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World world, LivingEntity entity, int tick) {
	}

	protected boolean canCastSpell(World world, PlayerEntity player, Hand hand, ItemStack stack) {
		return player.getHeldItem(Hand.MAIN_HAND).isEmpty();
	}

}
