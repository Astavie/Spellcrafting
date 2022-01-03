package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeCharm;
import astavie.spellcrafting.api.util.ItemList;

public class EventWaitFor implements NodeCharm {

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?>[] { SpellType.ANY, SpellType.ANY };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        SpellType<?> out = spell.getActualInputType(new Spell.Socket(node, 0));
        return new SpellType<?>[] { out == null ? SpellType.NONE : out };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public @NotNull Object[] cast(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        return new Object[] { input[0] };
    }
    
}
