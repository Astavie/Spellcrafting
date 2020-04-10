package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IArgumentType;
import astavie.spellcrafting.api.spell.IArgumentTypes;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;

public class ArgumentTypes implements IArgumentTypes {

	private final EntityArgument entity = new EntityArgument();

	@Override
	public IArgumentType<Entity> entity() {
		return entity;
	}

	@Override
	public IArgumentType<Location> location() {
		return null;
	}

}
