package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.IFocusTypes;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;

public class FocusTypes implements IFocusTypes {

	private final EntityType entity = new EntityType();
	private final LocationType location = new LocationType();
	private final CasterType caster = new CasterType();

	@Override
	public IFocusType<Entity> entity() {
		return entity;
	}

	@Override
	public IFocusType<Location> location() {
		return location;
	}

	@Override
	public IFocusType<ICaster> caster() {
		return caster;
	}

}
