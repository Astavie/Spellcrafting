package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;

public interface IFocusTypes {

	IFocusType<Entity> entity();

	IFocusType<Location> location();

	IFocusType<ICaster> caster();

}
