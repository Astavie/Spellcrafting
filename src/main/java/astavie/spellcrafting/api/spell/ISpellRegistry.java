package astavie.spellcrafting.api.spell;

import net.minecraft.util.ResourceLocation;

import java.util.Collection;

public interface ISpellRegistry {

	// Beads

	void registerBead(ResourceLocation location, IBead bead);

	IBead getBead(ResourceLocation location);

	Collection<IBead> getBeads();

	// Focuss

	void registerFocus(ResourceLocation location, IFocus<?> focus);

	IFocus<?> getFocus(ResourceLocation location);

	Collection<IFocus<?>> getFoci();

	// Augments

	void registerAugment(ResourceLocation location, IAugment<?> focus);

	IAugment<?> getAugment(ResourceLocation location);

	Collection<IAugment<?>> getAugments();

}
