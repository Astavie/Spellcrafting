package astavie.spellcrafting.api.spell;

import net.minecraft.nbt.INBT;

import javax.annotation.Nullable;

public interface IFocusType<I> {

	INBT writeToNBT(I object);

	@Nullable
	I readFromNBT(INBT nbt);

}
