package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.spell.caster.ICaster;

public interface ISpell {

	/**
	 * @return the object of an focus of a bead
	 */
	Object getFocus(IBeadStack stack, int focus);

	/**
	 * @return the caster of the spell
	 */
	ICaster getCaster();

	/**
	 * @return the spell template
	 */
	ISpellTemplate getSpellTemplate();

	/**
	 * @return the index of the current bead the spell is at
	 */
	int getPosition();

	/**
	 * Set the current position to the index of a bead
	 */
	void setPosition(int position);

	/**
	 * Cast the spell
	 */
	void cast();

	/**
	 * Tick the spell
	 */
	void tick();

	/**
	 * @return if the spell has finished casting
	 */
	boolean isFinished();

	/**
	 * @return if the spell is waiting for a "Cast" event
	 */
	boolean isWaitingForCast();

}
