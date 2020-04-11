package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.util.Location;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public interface IBead {

	/**
	 * @return the amount of foci this bead has
	 */
	int getFocusCount();

	/**
	 * @param focus the index of the focus
	 * @return what focus types can be inserted into the focus
	 */
	List<IFocusType<?>> getApplicableTypes(int focus);

	/**
	 * @param focus the index of the focus
	 * @return the fixed focus at the specified index, if there is one
	 */
	@Nullable
	default IFocusStack getFixedFocus(int focus) {
		return null;
	}

	/**
	 * @param spell     the spell this bead is in
	 * @param stack     the stack this bead is in
	 * @param castEvent whether or not the spell was triggered by a manual cast
	 * @return whether or not to continue after this bead
	 */
	default boolean shouldContinue(ISpell spell, IBeadStack stack, boolean castEvent) {
		return true;
	}

	@Nullable
	default Location getLocation(ISpell spell, IBeadStack stack) {
		return null;
	}

	/**
	 * @param spell the spell this bead is in
	 * @param stack the stack this bead is in
	 * @return if this bead was casted successfully
	 */
	boolean cast(ISpell spell, IBeadStack stack);

	ResourceLocation getRegistryName();

}
