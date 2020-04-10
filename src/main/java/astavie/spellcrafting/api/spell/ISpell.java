package astavie.spellcrafting.api.spell;

import javax.annotation.Nullable;
import java.util.List;

public interface ISpell {

	/**
	 * @return the list of beads of this spell
	 */
	List<IBeadStack> getBeads();

	/**
	 * @param id the id of the bead stack
	 * @return the bead stack that has the specified id
	 */
	@Nullable
	IBeadStack getBead(int id);

}
