package astavie.spellcrafting.api.spell;

import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.List;

public interface IArgumentStack {

	// REFERENCE

	/**
	 * Sets the argument of this stack to a reference of an argument of a previous bead
	 *
	 * @param stack a previous bead
	 * @param argument the index of the argument in the bead
	 */
	void setArgument(IBeadStack stack, int argument);

	/**
	 * @return a reference to the argument of a previous bead, if this stack has one
	 */
	@Nullable
	Pair<IBeadStack, Integer> getReference(ISpell spell);

	// OBJECT

	/**
	 * Sets the argument of this stack to an argument object
	 */
	void setArgument(IArgument argument);

	/**
	 * @return an argument object, if this stack has one
	 */
	@Nullable
	IArgument getArgument();

	// ---

	/**
	 * @return the modifiers that are applied to the argument
	 */
	List<IModifier> getModifiers();

	/**
	 * @return the type of this stack
	 */
	IArgumentType<?> getType(ISpell spell);

}
