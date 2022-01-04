package astavie.spellcrafting.item;

import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Focus;
import astavie.spellcrafting.api.spell.target.Target;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;

public class ItemMirror extends Item implements Focus {

    public ItemMirror() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public Target redirectTarget(Caster caster, Target original) {
        return caster.asTarget();
    }
    
}