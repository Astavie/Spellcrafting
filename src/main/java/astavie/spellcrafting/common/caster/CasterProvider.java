package astavie.spellcrafting.common.caster;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.ICaster;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CasterProvider implements ICapabilityProvider {

	private final LazyOptional<ICaster> caster;

	public CasterProvider(ICaster caster) {
		this.caster = LazyOptional.of(() -> caster);
	}

	@SubscribeEvent
	public static void onAttachCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof ServerPlayerEntity) { // Only do this on the server
			event.addCapability(new ResourceLocation(Spellcrafting.MODID, "caster"), new CasterProvider(new CasterPlayer((PlayerEntity) event.getObject())));
		}
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		return CasterCapability.CASTER_CAPABILITY.orEmpty(cap, caster);
	}

}
