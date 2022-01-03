package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.util.DyeColor;

public class TransmuterDesequencer implements NodeType {

    private static int AMOUNT = 2;

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?>[] { SpellType.ANY };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        SpellType<?> out = spell.getActualInputType(new Spell.Socket(node, 0));
        if (out == null) out = SpellType.NONE;

        SpellType<?>[] types = new SpellType<?>[AMOUNT];
        for (int i = 0; i < AMOUNT; i++) types[i] = out;
        return types;
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public @NotNull void apply(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        int index = node.channel().ordinal() & (AMOUNT >> 1);
        DyeColor out = DyeColor.values()[node.channel().ordinal() & (~(AMOUNT >> 1))];
        spell.apply(new Spell.ChannelSocket(new Spell.Socket(node.node(), index), out), spell.getInput(node)[0]);
    }

}
