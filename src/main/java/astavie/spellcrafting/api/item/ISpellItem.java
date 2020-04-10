package astavie.spellcrafting.api.item;

import astavie.spellcrafting.api.spell.caster.ICaster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public interface ISpellItem {

	boolean canCastSpell(PlayerEntity player, Hand hand, ItemStack stack);

	void castSpell(ICaster caster, ItemStack stack);

}
