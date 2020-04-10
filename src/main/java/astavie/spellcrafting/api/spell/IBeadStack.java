package astavie.spellcrafting.api.spell;

public interface IBeadStack {

	/**
	 * Used to reference beads from {@link IFocusStack}s
	 *
	 * @return the id of this stack
	 */
	int getId();

	/**
	 * @return the bead of this stack
	 */
	IBead getBead();

	/**
	 * @param index the index of the focus
	 * @return the focus at the specified index
	 */
	IFocusStack getFocus(int index);

	/**
	 * @param index the index of the focus
	 * @param stack the focus that will be placed at the specified index
	 */
	void setFocus(int index, IFocusStack stack);

}
