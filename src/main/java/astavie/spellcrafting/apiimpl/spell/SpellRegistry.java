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
	public void registerBead(IBead bead) {
		beads.put(bead.getRegistryName(), bead);
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
	public void registerFocus(IFocus<?> focus) {
		foci.put(focus.getRegistryName(), focus);
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
	public void registerAugment(IAugment<?> augment) {
		augments.put(augment.getRegistryName(), augment);
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
