package astavie.spellcrafting.common;

import astavie.spellcrafting.Spellcrafting;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.Nonnull;

public class SpellManager extends WorldSavedData {

	private static final String DATA_NAME = Spellcrafting.MODID;

	public SpellManager() {
		super(DATA_NAME);
	}

	public static SpellManager getManager(ServerWorld world) {
		return world.getSavedData().getOrCreate(SpellManager::new, DATA_NAME);
	}

	@Override
	public void read(@Nonnull CompoundNBT nbt) {

	}

	@Nonnull
	@Override
	public CompoundNBT write(@Nonnull CompoundNBT compound) {
		return null;
	}

}
