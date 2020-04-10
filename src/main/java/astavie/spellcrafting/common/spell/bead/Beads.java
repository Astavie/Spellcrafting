package astavie.spellcrafting.common.spell.bead;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.ISpellRegistry;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.util.ResourceLocation;

public class Beads {

	public static final IBead SWAP = new BeadSwap();

	private Beads() {
	}

	public static void register() {
		ISpellRegistry registry = SpellcraftingAPI.instance().spellRegistry();

		registry.registerBead(new ResourceLocation(Spellcrafting.MODID, "swap"), SWAP);
	}

}
