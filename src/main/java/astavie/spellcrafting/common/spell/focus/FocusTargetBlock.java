package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.api.util.Location;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.util.ResourceLocation;

public class FocusTargetBlock implements IFocus<Location> {

	@Override
	public IFocusType<Location> getType() {
		return SpellcraftingAPI.instance().focusTypes().location();
	}

	@Override
	public Location calculate(ISpell spell) {
		ICaster caster = spell.getCaster();
		if (caster == null)
			return null;
		return caster.getTargetLocation(spell);
	}

	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(Spellcrafting.MODID, "target_block");
	}

}
