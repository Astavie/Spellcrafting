package astavie.spellcrafting.common.item;

import astavie.spellcrafting.api.item.ISpellItem;
import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.IFocusStack;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.ISpellTemplate;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.common.network.PacketHandler;
import astavie.spellcrafting.common.network.server.MessageCast;
import astavie.spellcrafting.common.spell.bead.Beads;
import astavie.spellcrafting.common.spell.event.Events;
import astavie.spellcrafting.common.spell.focus.Foci;
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

		if (world.isRemote && canCastSpell(player, hand, stack)) {
			PacketHandler.sendToServer(MessageCast.createMessage(player, hand, 10)); // TODO: Reach
		}

		return ActionResult.resultPass(stack);
	}

	@Override
	public boolean canCastSpell(PlayerEntity player, Hand hand, ItemStack stack) {
		return player.getHeldItem(Hand.MAIN_HAND).isEmpty();
	}

	@Override
	public void castSpell(ICaster caster, ItemStack stack) {
		// TODO: Test
		ISpellTemplate template = SpellcraftingAPI.instance().createSpellTemplate();

		IBeadStack cast = SpellcraftingAPI.instance().createBeadStack(0, Events.CAST);
		template.getBeads().add(cast);

		IFocusStack f0 = SpellcraftingAPI.instance().createFocusStack();
		f0.setFocus(cast, 1);

		IFocusStack f1 = SpellcraftingAPI.instance().createFocusStack();
		f1.setFocus(Foci.CASTER);

		IBeadStack swap = SpellcraftingAPI.instance().createBeadStack(1, Beads.SWAP);
		swap.setFocus(0, f0);
		swap.setFocus(1, f1);
		template.getBeads().add(swap);

		ISpell spell = SpellcraftingAPI.instance().createSpell(caster, template);
		spell.cast();
	}

}
