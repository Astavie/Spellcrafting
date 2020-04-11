package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.ISpellRegistry;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;

public class Foci {

	public static final IFocus<?> CASTER = new FocusCaster();
	public static final IFocus<?> TARGET_BLOCK = new FocusTargetBlock();
	public static final IFocus<?> TARGET_ENTITY = new FocusTargetEntity();

	private Foci() {
	}

	public static void register() {
		ISpellRegistry registry = SpellcraftingAPI.instance().spellRegistry();

		registry.registerFocus(CASTER);
		registry.registerFocus(TARGET_BLOCK);
		registry.registerFocus(TARGET_ENTITY);
	}

}
