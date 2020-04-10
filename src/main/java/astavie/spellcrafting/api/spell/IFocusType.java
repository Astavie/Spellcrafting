package astavie.spellcrafting.api.spell;

import net.minecraft.nbt.INBT;

public interface IFocusType<I> {

	INBT writeToNBT(I object);

	I readFromNBT(INBT nbt);

}
