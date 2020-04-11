package astavie.spellcrafting.common.spell.event;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.api.util.Location;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.common.spell.focus.Foci;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class EventCast implements IEvent {

	private final IFocusStack targetBlock, targetEntity;

	public EventCast() {
		targetBlock = SpellcraftingAPI.instance().createFocusStack();
		targetBlock.setFocus(Foci.TARGET_BLOCK);

		targetEntity = SpellcraftingAPI.instance().createFocusStack();
		targetEntity.setFocus(Foci.TARGET_ENTITY);
	}

	@Override
	public int getFocusCount() {
		return 2;
	}

	@Override
	public List<IFocusType<?>> getApplicableTypes(int focus) {
		switch (focus) {
			case 0:
				return Collections.singletonList(SpellcraftingAPI.instance().focusTypes().location());
			case 1:
				return Collections.singletonList(SpellcraftingAPI.instance().focusTypes().entity());
			default:
				return Collections.emptyList();
		}
	}

	@Nullable
	@Override
	public IFocusStack getFixedFocus(int focus) {
		switch (focus) {
			case 0:
				return targetBlock;
			case 1:
				return targetEntity;
			default:
				return null;
		}
	}

	@Override
	public boolean shouldContinue(ISpell spell, IBeadStack stack, boolean castEvent) {
		return castEvent;
	}

	@Nullable
	@Override
	public Location getLocation(ISpell spell, IBeadStack stack) {
		ICaster caster = spell.getCaster();
		if (caster == null)
			return null;

		return caster.getFeet();
	}

	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(Spellcrafting.MODID, "cast");
	}

}
