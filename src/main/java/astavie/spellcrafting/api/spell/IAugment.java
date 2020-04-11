package astavie.spellcrafting.api.spell;

import net.minecraft.util.ResourceLocation;

import java.util.List;

public interface IAugment<I> {

	/**
	 * @return the focus types this augment can augment
	 */
	List<IFocusType<?>> getApplicableTypes();

	/**
	 * @return the output type after augmentation
	 */
	IFocusType<I> getType();

	/**
	 * @param spell  the spell this is happening in
	 * @param parent the object this augment is augmenting
	 * @return the augmented object
	 */
	I apply(ISpell spell, Object parent);

	/**
	 * @param type the type of the focus this augment is supposed to augment
	 * @return whether of not this augment can augment the focus
	 */
	default boolean isApplicable(IFocusType<?> type) {
		return getApplicableTypes().contains(type);
	}

	ResourceLocation getRegistryName();

}
