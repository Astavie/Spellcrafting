package astavie.spellcrafting.api.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemList implements Iterable<Map.Entry<ItemVariant, Long>> {

    private final Map<ItemVariant, Long> items = new LinkedHashMap<>();

    public int size() {
        return items.size();
    }

    public long get(Item item) {
        return get(ItemVariant.of(item));
    }

    public long get(ItemStack item) {
        return get(ItemVariant.of(item));
    }

    public long get(ItemVariant item) {
        return items.containsKey(item) ? items.get(item) : 0;
    }
    
    public ItemList addItem(@NotNull Item item) {
        addItem(ItemVariant.of(item), 1);
        return this;
    }
    
    public ItemList addItem(@NotNull Item item, long amount) {
        addItem(ItemVariant.of(item), amount);
        return this;
    }
    
    public ItemList addItem(@NotNull ItemStack stack) {
        addItem(ItemVariant.of(stack), stack.getCount());
        return this;
    }

    public ItemList addItem(@NotNull ItemVariant variant, long amount) {
        long l = items.getOrDefault(variant, 0L);
        items.put(variant, l + amount);
        return this;
    }

    public void addItemList(@NotNull ItemList list) {
        for (Map.Entry<ItemVariant, Long> entry : list) {
            addItem(entry.getKey(), entry.getValue());
        }
    }

    public void addItemList(@NotNull ItemList list, int factor) {
        for (Map.Entry<ItemVariant, Long> entry : list) {
            addItem(entry.getKey(), entry.getValue() * factor);
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public @NotNull Iterator<Map.Entry<ItemVariant, Long>> iterator() {
        return items.entrySet().iterator();
    }

}
