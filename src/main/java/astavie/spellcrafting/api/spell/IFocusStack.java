package astavie.spellcrafting.api.spell;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public interface IFocusStack {

	// REFERENCE

	/**
	 * Sets the focus of this stack to a reference of an focus of a previous bead
	 *
	 * @param stack a previous bead
	 * @param focus the index of the focus in the bead
	 */
	void setFocus(IBeadStack stack, int focus);

	/**
	 * @return a reference to the focus of a previous bead, if this stack has one
	 */
	@Nullable
	Pair<IBeadStack, Integer> getReference(ISpellTemplate spell);

	// OBJECT

	/**
	 * @return an focus object, if this stack has one
	 */
	@Nullable
	IFocus getFocus();

	/**
	 * Sets the focus of this stack to an focus object
	 */
	void setFocus(IFocus<?> focus);

	// ---

	/**
	 * @return the augments that are applied to the focus
	 */
	List<IAugment<?>> getAugments();

	/**
	 * @return the type of this stack
	 */
	IFocusType<?> getType(ISpellTemplate spell);

	/**
	 * @param spell the spell this is happening in
	 * @return the object
	 */
	Object calculate(ISpell spell);

}
