package astavie.spellcrafting.api.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;

public interface NodeTransmuter extends NodeType {

    @Override
    default void onOn(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        Object[] input = spell.getInput(node);
        spell.apply(node, transmute(spell, node, input));
    }

    @Override
    default void onOff(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        Object[] input = spell.getInput(node);
        spell.apply(node, transmute(spell, node, input));
    }

    @NotNull Object[] transmute(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input);
    
}
