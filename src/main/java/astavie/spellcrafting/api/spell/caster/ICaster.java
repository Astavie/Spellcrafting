package astavie.spellcrafting.api.spell.caster;

import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;

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
	 * @return if the caster is a block
	 */
	boolean isBlock();

	/**
	 * @return if the caster is an entity
	 */
	boolean isEntity();

	/**
	 * @return the caster as a tile entity, if it is one
	 */
	@Nullable
	TileEntity getAsBlock();

	/**
	 * @return the caster as an entity, if it is one
	 */
	@Nullable
	Entity getAsEntity();

	/**
	 * @return the target entity
	 */
	@Nullable
	Entity getTargetEntity(ISpell info);

	/**
	 * @return the target location
	 */
	@Nullable
	Location getTargetLocation(ISpell info);

}
