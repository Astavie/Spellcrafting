package astavie.spellcrafting.common.spell.focus;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.IFocus;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class FocusTargetEntity implements IFocus<Entity> {

	@Override
	public IFocusType<Entity> getType() {
		return SpellcraftingAPI.instance().focusTypes().entity();
	}

	@Override
	public Entity calculate(ISpell spell) {
		ICaster caster = spell.getCaster();
		if (caster == null)
			return null;
		return caster.getTargetEntity(spell);
	}

	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(Spellcrafting.MODID, "target_entity");
	}

}
