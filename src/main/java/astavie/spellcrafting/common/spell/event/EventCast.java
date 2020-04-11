package astavie.spellcrafting.common.spell.event;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.*;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.common.spell.focus.Foci;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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

	@Override
	public boolean cast(ISpell spell, IBeadStack stack) {
		spell.setCenter(Objects.requireNonNull(spell.getCaster()).getFeet());
		return true;
	}

	@Override
	public boolean isLoaded(ISpell spell, IBeadStack stack) {
		return spell.getCaster() != null;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(Spellcrafting.MODID, "cast");
	}

}
