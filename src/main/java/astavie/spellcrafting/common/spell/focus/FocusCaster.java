package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;

public class FocusCaster implements IFocus<ICaster> {

	@Override
	public IFocusType<ICaster> getType() {
		return SpellcraftingAPI.instance().focusTypes().caster();
	}

	@Override
	public ICaster calculate(ISpell spell) {
		return spell.getCaster();
	}

}
