package astavie.spellcrafting.common.caster;

import astavie.spellcrafting.api.spell.caster.ICaster;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;

public class CasterCapability {

	@CapabilityInject(ICaster.class)
	public static Capability<ICaster> CASTER_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(ICaster.class, new Storage(), new Factory());
	}

	private static class Storage implements Capability.IStorage<ICaster> {

		@Nullable
		@Override
		public INBT writeNBT(Capability<ICaster> capability, ICaster instance, Direction side) {
			return null;
		}

		@Override
		public void readNBT(Capability<ICaster> capability, ICaster instance, Direction side, INBT nbt) {
		}

	}

	private static class Factory implements Callable<ICaster> {

		@Override
		public ICaster call() {
			throw new UnsupportedOperationException("Cannot use default implementation");
		}

	}

}
