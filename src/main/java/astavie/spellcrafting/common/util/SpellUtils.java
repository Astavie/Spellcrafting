package astavie.spellcrafting.common.util;

import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.caster.ICaster;
import net.minecraft.entity.Entity;

public class SpellUtils {

	private SpellUtils() {
	}

	public static Entity getEntity(ISpell spell, IBeadStack stack, int focus) {
		Object object = spell.getFocus(stack, focus);

		if (object instanceof Entity) {
			return (Entity) object;
		} else if (object instanceof ICaster) {
			return ((ICaster) object).getAsEntity();
		}

		return null;
	}

}
