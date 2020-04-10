package astavie.spellcrafting.common.caster;

import astavie.spellcrafting.api.spell.ICaster;
import astavie.spellcrafting.api.spell.ISpellInfo;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

public class CasterPlayer implements ICaster {

	private final PlayerEntity entity;

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
	public Entity getTargetEntity(ISpellInfo info) {
		return null;
	}

	@Nullable
	@Override
	public Location getTargetLocation(ISpellInfo info) {
		return null;
	}

}
