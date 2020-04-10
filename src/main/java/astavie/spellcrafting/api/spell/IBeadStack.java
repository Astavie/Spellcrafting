package astavie.spellcrafting.api.spell;

public interface IBeadStack {

	/**
	 * Used to reference beads from {@link IArgumentStack}s
	 *
	 * @return the id of this stack
	 */
	int getId();

	/**
	 * @return the bead of this stack
	 */
	IBead getBead();

	/**
	 * @param index the index of the argument
	 * @return the argument at the specified index
	 */
	IArgumentStack getArgument(int index);

	/**
	 * @param index the index of the argument
	 * @param stack the argument that will be placed at the specified index
	 */
	void setArgument(int index, IArgumentStack stack);

}
