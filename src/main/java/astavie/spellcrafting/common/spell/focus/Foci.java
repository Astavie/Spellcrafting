package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.ISpellRegistry;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.util.ResourceLocation;

public class Foci {

	public static final IFocus<?> CASTER = new FocusCaster();
	public static final IFocus<?> TARGET_BLOCK = new FocusTargetBlock();
	public static final IFocus<?> TARGET_ENTITY = new FocusTargetEntity();

	private Foci() {
	}

	public static void register() {
		ISpellRegistry registry = SpellcraftingAPI.instance().spellRegistry();

		registry.registerFocus(new ResourceLocation(Spellcrafting.MODID, "caster"), CASTER);
		registry.registerFocus(new ResourceLocation(Spellcrafting.MODID, "target_block"), TARGET_BLOCK);
		registry.registerFocus(new ResourceLocation(Spellcrafting.MODID, "target_entity"), TARGET_ENTITY);
	}

}
