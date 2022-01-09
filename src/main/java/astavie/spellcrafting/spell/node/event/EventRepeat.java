package astavie.spellcrafting.spell.node.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.util.ItemList;

public class EventRepeat implements NodeCharm {

    // TODO: Variable timing
    // TODO: Variable amount
    // TODO: Component factor

    private static final int AMOUNT = 4;

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType<?>[] { SpellType.ANY };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        SpellType<?> out = spell.getActualInputType(new Spell.Socket(node, 0));
        return new SpellType<?>[] { out == null ? SpellType.NONE : out };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList();
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        spell.scheduleFor(node, AMOUNT * 2 - 1);
        return new Object[1];
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Spell.Event type, @Nullable Object context) {
        if (spell.getOutput(new Spell.ChannelSocket(node.node(), 0, node.channel())) == null) {
            spell.apply(node, spell.getInput(node));
        } else {
            spell.apply(node, new Object[1]);
        }
    }

    @Override
    public boolean matches(int size, ItemList recipe) {
        return false; // TODO: Recipe
    }
    
}
