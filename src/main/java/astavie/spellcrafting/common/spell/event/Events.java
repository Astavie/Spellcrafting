package astavie.spellcrafting.common.spell.event;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.ISpellRegistry;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.util.ResourceLocation;

public class Events {

	public static final IBead CAST = new EventCast();

	private Events() {
	}

	public static void register() {
		ISpellRegistry registry = SpellcraftingAPI.instance().spellRegistry();

		registry.registerBead(new ResourceLocation(Spellcrafting.MODID, "cast"), CAST);
	}

}
