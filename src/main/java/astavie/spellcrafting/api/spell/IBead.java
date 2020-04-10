package astavie.spellcrafting.api.spell;

import javax.annotation.Nullable;
import java.util.List;

public interface IBead {

	/**
	 * @return the amount of arguments this bead has
	 */
	int getArgumentCount();

	/**
	 * @param argument the index of the argument
	 * @return what argument types can be inserted into the argument
	 */
	List<IArgumentType<?>> getApplicableTypes(int argument);

	/**
	 * @param argument the index of the argument
	 * @return the fixed argument at the specified index, if there is one
	 */
	@Nullable
	default IArgumentStack getForcedArgument(int argument) {
		return null;
	}

}
