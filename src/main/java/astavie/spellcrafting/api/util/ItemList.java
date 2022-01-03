package astavie.spellcrafting.api.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemList implements Iterable<Entry<ItemVariant, Long>> {

    private final Map<ItemVariant, Long> items = new LinkedHashMap<>();
    
    public void addItem(@NotNull Item item) {
        addItem(ItemVariant.of(item), 1);
    }
    
    public void addItem(@NotNull Item item, long amount) {
        addItem(ItemVariant.of(item), amount);
    }
    
    public void addItem(@NotNull ItemStack stack) {
        addItem(ItemVariant.of(stack), stack.getCount());
    }

    public void addItem(@NotNull ItemVariant variant, long amount) {
        long l = items.getOrDefault(variant, 0L);
        items.put(variant, l + amount);
    }

    public void addItemList(@NotNull ItemList list) {
        for (Entry<ItemVariant, Long> entry : list) {
            addItem(entry.getKey(), entry.getValue());
        }
    }

    public void addItemList(@NotNull ItemList list, int factor) {
        for (Entry<ItemVariant, Long> entry : list) {
            addItem(entry.getKey(), entry.getValue() * factor);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public @NotNull Iterator<Entry<ItemVariant, Long>> iterator() {
        return items.entrySet().iterator();
    }

}
