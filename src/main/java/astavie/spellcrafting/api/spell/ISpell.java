package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.ISpellcraftingAPI;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;

public interface ISpell {

	/**
	 * @return the object of an focus of a bead
	 */
	@Nullable
	Object getFocus(IBeadStack stack, int focus);

	/**
	 * @return the caster of the spell
	 */
	@Nullable
	ICaster getCaster();

	/**
	 * @return the spell template
	 */
	ISpellTemplate getSpellTemplate();

	/**
	 * @return the index of the current bead the spell is at
	 */
	int getPosition();

	/**
	 * Set the current position to the index of a bead
	 */
	void setPosition(int position);

	/**
	 * Cast the spell
	 */
	void cast();

	/**
	 * Tick the spell
	 */
	void tick();

	/**
	 * @return if the spell has finished casting
	 */
	boolean isFinished();

	/**
	 * @return if the spell is waiting for a "Cast" event
	 */
	boolean isWaitingForCast();

	/**
	 * Read with {@link ISpellcraftingAPI#readSpell(CompoundNBT)}
	 */
	CompoundNBT writeToNbt();

	Location getCenter();

	void setCenter(Location location);

	boolean isLoaded();

}
