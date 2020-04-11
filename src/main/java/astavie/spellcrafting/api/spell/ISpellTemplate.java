package astavie.spellcrafting.api.spell;

import net.minecraft.nbt.ListNBT;

import javax.annotation.Nullable;
import java.util.List;

public interface ISpellTemplate {

	/**
	 * @return the list of beads of this spell template
	 */
	List<IBeadStack> getBeads();

	/**
	 * @param id the id of the bead stack
	 * @return the bead stack that has the specified id
	 */
	@Nullable
	IBeadStack getBeadFromId(int id);

	/**
	 * @return the position of a bead in the bead list
	 */
	int getPosition(IBeadStack stack);

	ListNBT writeToNbt();

	void readFromNbt(ListNBT nbt);

}
