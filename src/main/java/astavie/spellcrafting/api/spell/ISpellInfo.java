package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.apiimpl.spell.BeadStack;

public interface ISpellInfo {

	/**
	 * Calculates the object of an argument and saves it for later use
	 *
	 * @return the object of the argument of the bead stack
	 */
	<I> I calculateArgument(BeadStack stack, int argument);

	/**
	 * @return the object of the argument that was previously calculated
	 */
	<I> I getArgument(BeadStack stack, int argument);

	/**
	 * @return the caster of the spell
	 */
	ICaster getCaster();

	/**
	 * @return the casted spell
	 */
	ISpell getSpell();

	/**
	 * @return the index of the current bead the spell is at
	 */
	int getPosition();

}
