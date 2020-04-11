package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.util.Location;

import javax.annotation.Nullable;

public interface IEvent extends IBead {

	@Override
	boolean shouldContinue(ISpell spell, IBeadStack stack, boolean castEvent);

	@Nullable
	@Override
	Location getLocation(ISpell spell, IBeadStack stack);

	@Override
	default boolean cast(ISpell spell, IBeadStack stack) {
		return true;
	}

}
