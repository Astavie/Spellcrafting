package astavie.spellcrafting.common.caster;

import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.caster.ICasterEntity;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class CasterPlayer implements ICasterEntity {

	private final PlayerEntity entity;

	public Entity tEntity;
	public Location tLocation;

	public CasterPlayer(PlayerEntity entity) {
		this.entity = entity;
	}

	@Override
	public Location getHand() {
		return new Location(entity.dimension, entity.getEyePosition(1)); // TODO
	}

	@Override
	public Location getEyes() {
		return new Location(entity.dimension, entity.getEyePosition(1));
	}

	@Nullable
	@Override
	public Entity getAsEntity() {
		return entity;
	}

	@Nullable
	@Override
	public Entity getTargetEntity(ISpell info) {
		return tEntity;
	}

	@Nullable
	@Override
	public Location getTargetLocation(ISpell info) {
		return tLocation;
	}

}
