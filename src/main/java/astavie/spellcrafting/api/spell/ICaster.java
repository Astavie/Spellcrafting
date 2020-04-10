package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

/**
 * Makes a block or entity able to cast spells. Implement this as a capability. You can get the capability by using {@code @CapabilityInject(ICaster.class)}
 */
public interface ICaster {

	/**
	 * @return the location of the main hand of the caster
	 */
	Location getHand();

	/**
	 * @return the location of the eyes of the caster
	 */
	Location getEyes();

	/**
	 * @return the target entity
	 */
	@Nullable
	Entity getTargetEntity(ISpellInfo info);

	/**
	 * @return the target location
	 */
	@Nullable
	Location getTargetLocation(ISpellInfo info);

}
