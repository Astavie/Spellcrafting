package astavie.spellcrafting.common.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

public class ItemSpellTest extends ItemSpell {

	public ItemSpellTest() {
		super(new Properties().maxStackSize(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}

}
