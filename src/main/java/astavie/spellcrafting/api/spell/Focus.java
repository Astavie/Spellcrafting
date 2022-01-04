package astavie.spellcrafting.api.spell;

import astavie.spellcrafting.api.spell.target.Target;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.util.Identifier;

public interface Focus {

    ItemApiLookup<Focus, ContainerItemContext> ITEM_FOCUS = ItemApiLookup.get(new Identifier("spellcrafting:focus"), Focus.class, ContainerItemContext.class);

    Target redirectTarget(Caster caster, Target original);
    
}
