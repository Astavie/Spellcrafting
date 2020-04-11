package astavie.spellcrafting.api;

import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.api.spell.caster.ICaster;
import net.minecraft.nbt.CompoundNBT;

public interface ISpellcraftingAPI {

	IFocusStack createFocusStack();

	IBeadStack createBeadStack(int id, IBead bead);

	ISpellTemplate createSpellTemplate();

	ISpell createSpell(ICaster caster, ISpellTemplate spell);

	/**
	 * Write with {@link ISpell#writeToNbt()}
	 */
	ISpell readSpell(CompoundNBT nbt);

	IFocusTypes focusTypes();

	ISpellRegistry spellRegistry();

}
