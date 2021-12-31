package astavie.spellcrafting.api.item;

import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.ActiveSpell;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.Target;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.minecraft.util.Identifier;

public interface SpellContainer {

    ItemApiLookup<SpellContainer, Void> ITEM_SPELL = ItemApiLookup.get(new Identifier("spellcrafting:spell"), SpellContainer.class, Void.class);

    boolean isActive();

    @Nullable Spell getSpell();

    @Nullable ActiveSpell getActiveSpell();

    @Nullable ActiveSpell activate(Caster caster, Target target);

}
