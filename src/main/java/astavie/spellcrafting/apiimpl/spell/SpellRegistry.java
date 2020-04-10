package astavie.spellcrafting.apiimpl.spell;

import astavie.spellcrafting.api.spell.IAugment;
import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.ISpellRegistry;
import net.minecraft.util.ResourceLocation;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class SpellRegistry implements ISpellRegistry {

	private final Map<ResourceLocation, IBead> beads = new LinkedHashMap<>();
	private final Map<ResourceLocation, IFocus<?>> foci = new LinkedHashMap<>();
	private final Map<ResourceLocation, IAugment<?>> augments = new LinkedHashMap<>();

	@Override
	public void registerBead(ResourceLocation location, IBead bead) {
		beads.put(location, bead);
	}

	@Override
	public IBead getBead(ResourceLocation location) {
		return beads.get(location);
	}

	@Override
	public Collection<IBead> getBeads() {
		return Collections.unmodifiableCollection(beads.values());
	}

	@Override
	public void registerFocus(ResourceLocation location, IFocus<?> focus) {
		foci.put(location, focus);
	}

	@Override
	public IFocus<?> getFocus(ResourceLocation location) {
		return foci.get(location);
	}

	@Override
	public Collection<IFocus<?>> getFoci() {
		return Collections.unmodifiableCollection(foci.values());
	}

	@Override
	public void registerAugment(ResourceLocation location, IAugment<?> augment) {
		augments.put(location, augment);
	}

	@Override
	public IAugment<?> getAugment(ResourceLocation location) {
		return augments.get(location);
	}

	@Override
	public Collection<IAugment<?>> getAugments() {
		return Collections.unmodifiableCollection(augments.values());
	}

}
