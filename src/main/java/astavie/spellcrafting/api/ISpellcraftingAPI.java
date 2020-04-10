package astavie.spellcrafting.api;

import astavie.spellcrafting.api.spell.*;

public interface ISpellcraftingAPI {

	IArgumentStack createArgumentStack();

	IBeadStack createBeadStack(int id, IBead bead);

	ISpell createSpell();

	IArgumentTypes argumentTypes();

}
