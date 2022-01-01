package astavie.spellcrafting.api.item;

import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.util.Identifier;

public interface SpellContainer {

    ItemApiLookup<SpellContainer, ContainerItemContext> ITEM_SPELL = ItemApiLookup.get(new Identifier("spellcrafting:spell"), SpellContainer.class, ContainerItemContext.class);

    @Nullable Spell getSpell();

}
