package astavie.spellcrafting.api.spell.caster;

import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;

public interface ICasterEntity extends ICaster {

	@Override
	default boolean isBlock() {
		return false;
	}

	@Override
	default boolean isEntity() {
		return true;
	}

	@Nullable
	@Override
	default TileEntity getAsBlock() {
		return null;
	}

}
