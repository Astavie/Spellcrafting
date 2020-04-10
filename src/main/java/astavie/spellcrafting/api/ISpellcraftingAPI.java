package astavie.spellcrafting.api;

import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.api.spell.caster.ICaster;

public interface ISpellcraftingAPI {

	IFocusStack createFocusStack();

	IBeadStack createBeadStack(int id, IBead bead);

	ISpellTemplate createSpellTemplate();

	ISpell createSpell(ICaster caster, ISpellTemplate spell);

	IFocusTypes focusTypes();

	ISpellRegistry spellRegistry();

}
