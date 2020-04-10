package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;

public interface IArgumentTypes {

	IArgumentType<Entity> entity();

	IArgumentType<Location> location();

}
