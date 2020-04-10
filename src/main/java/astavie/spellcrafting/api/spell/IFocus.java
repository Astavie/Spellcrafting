package astavie.spellcrafting.api.spell;

public interface IFocus<I> {

	/**
	 * @return the type of this focus
	 */
	IFocusType<I> getType();

	/**
	 * @param spell the spell this is happening in
	 * @return the object
	 */
	I calculate(ISpell spell);

}
