package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.util.Location;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;

public class FocusTargetBlock implements IFocus<Location> {

	@Override
	public IFocusType<Location> getType() {
		return SpellcraftingAPI.instance().focusTypes().location();
	}

	@Override
	public Location calculate(ISpell spell) {
		return spell.getCaster().getTargetLocation(spell);
	}

}
