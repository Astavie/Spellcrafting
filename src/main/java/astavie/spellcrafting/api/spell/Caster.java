package astavie.spellcrafting.api.spell;

import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.util.ItemList;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public interface Caster {

    BlockApiLookup<Caster, Direction> BLOCK_CASTER = BlockApiLookup.get(new Identifier("spellcrafting:caster"), Caster.class, Direction.class);
    EntityApiLookup<Caster, Void> ENTITY_CASTER = EntityApiLookup.get(new Identifier("spellcrafting:caster"), Caster.class, Void.class);

    @NotNull Target asTarget();

    @NotNull Storage<ItemVariant> getComponentStorage();

    double getRange();

    /**
     * @param list The list of items to extract
     * @param transaction The transaction this extraction is a part of
     * @return The list of missing items
     */
    default ItemList useComponents(ItemList list, Transaction transaction) {
        ItemList missing = new ItemList();
        Storage<ItemVariant> pouch = getComponentStorage();

        for (Entry<ItemVariant, Long> entry : list) {
            long extracted = pouch.extract(entry.getKey(), entry.getValue(), transaction);
            if (extracted < entry.getValue()) {
                missing.addItem(entry.getKey(), entry.getValue() - extracted);
            }
        }

        return missing;
    }
    
}
