package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.entity.Entity;

public class FocusTargetEntity implements IFocus<Entity> {

	@Override
	public IFocusType<Entity> getType() {
		return SpellcraftingAPI.instance().focusTypes().entity();
	}

	@Override
	public Entity calculate(ISpell spell) {
		return spell.getCaster().getTargetEntity(spell);
	}

}
