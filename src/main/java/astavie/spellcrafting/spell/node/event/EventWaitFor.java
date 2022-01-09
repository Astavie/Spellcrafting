package astavie.spellcrafting.spell.node.event;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;

public class EventWaitFor implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        SpellType<?>[] types = new SpellType<?>[node.getSize()];
        for (int i = 0; i < node.getSize(); i++) types[i] = SpellType.ANY;
        return types;
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        SpellType<?>[] types = new SpellType<?>[getParameters(node).length];

        for (int i = 0; i < types.length; i++) {
            SpellType<?> out = spell.getActualInputType(new Spell.Socket(node, i));
            types[i] = out == null ? SpellType.NONE : out;
        }

        return types;
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList();
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        return input;
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return size > 1 && recipe.size() == 1 && recipe.get(Items.CLOCK) == 1;
    }
    
}
