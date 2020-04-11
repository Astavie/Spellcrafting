package astavie.spellcrafting.api.spell;

import net.minecraft.nbt.CompoundNBT;

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

	double getRange();

	void setRange(double range);

	CompoundNBT writeToNbt();

	void readFromNbt(CompoundNBT nbt);

}
