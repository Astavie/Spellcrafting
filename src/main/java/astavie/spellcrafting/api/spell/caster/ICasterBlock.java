package astavie.spellcrafting.api.spell.caster;

import net.minecraft.entity.Entity;

import javax.annotation.Nullable;

public interface ICasterBlock extends ICaster {

	@Override
	default boolean isBlock() {
		return true;
	}

	@Override
	default boolean isEntity() {
		return false;
	}

	@Nullable
	@Override
	default Entity getAsEntity() {
		return null;
	}

}
