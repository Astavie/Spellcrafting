package astavie.spellcrafting.spell.node;

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
        spell.schedule(node);
        return input;
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Spell.Event type, @Nullable Object context) {
        spell.schedule(node);
        
        if (spell.getOutput(new Spell.ChannelSocket(node.node(), 0, node.channel())) == null) {
            spell.apply(node, spell.getInput(node));
        } else {
            spell.apply(node, new Object[] { null });
        }
    }
    
}
