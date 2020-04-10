package astavie.spellcrafting.api.spell;

import java.util.List;

public interface IModifier {

	/**
	 * @return the argument types this modifier can modify
	 */
	List<IArgumentType<?>> getApplicableTypes();

	/**
	 * @return the output type after modification
	 */
	IArgumentType<?> getType();

	/**
	 * @param type the type of the argument this modifier is supposed to modify
	 * @return whether of not this modifier can modify the argument
	 */
	default boolean isApplicable(IArgumentType<?> type) {
		return getApplicableTypes().contains(type);
	}

}
