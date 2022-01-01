package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.Spell.Event;
import astavie.spellcrafting.api.spell.node.SpellNode;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.nbt.NbtLong;

public class NodeEnd implements SpellNode {

    @Override
    public boolean applyOnChange() {
        return false;
    }

    @Override
    public @NotNull SpellType[] parameters() {
        return new SpellType[] { SpellType.TIME };
    }

    @Override
    public @NotNull SpellType[] returnTypes() {
        return new SpellType[0];
    }

    @Override
    public @NotNull ItemList components() {
        return new ItemList();
    }

    @Override
    public @NotNull Object[] apply(@NotNull Spell spell, @NotNull Object[] input) {
        if (input[0] != null) {
            spell.registerEvent(new Spell.Event<>(Spell.Event.TICK_ID, NbtLong.of(spell.getTime() + 1)), this);
        }
        return new Object[0];
    }

    @Override
    public <T> void onEvent(@NotNull Spell spell, @NotNull Object[] input, Event<T> type, T context) {
        spell.end();
    }
    
}
