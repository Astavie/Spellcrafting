package astavie.spellcrafting.api.spell;

import net.minecraft.nbt.INBT;

public interface IArgumentType<I> {

	INBT writeToNBT(I object);

	I readFromNBT(INBT nbt);

}
