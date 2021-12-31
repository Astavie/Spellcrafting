package astavie.spellcrafting.api.spell;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.util.ItemList;

public class SubSpell {

    private final List<CharmStack> stacks = new LinkedList<>();

    public @NotNull List<CharmStack> getCharms() {
        return stacks;
    }

    public void cast(@NotNull ActiveSpell context, int offset) {
        for (int i = 0; i < stacks.size(); i++) {
            CharmStack charm = stacks.get(i);
            charm.cast(context, offset + i);
            if (charm.getCharm().isBlocking()) return;
        }
    }

    public @NotNull ItemList getComponents() {
        ItemList list = new ItemList();
        for (CharmStack stack : stacks) {
            list.addItemList(stack.getCharm().getComponents());
        }
        return list;
    }
    
}
