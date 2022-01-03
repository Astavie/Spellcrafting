package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeEvent;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.api.util.ServerUtils;
import net.minecraft.nbt.NbtLong;

public class EventWait implements NodeEvent<Void> {

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType<?>[] { SpellType.ANY };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        SpellType<?> out = spell.getActualInputType(new Spell.Socket(node, 0));
        return new SpellType<?>[] { out == null ? SpellType.NONE : out };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components
    }

    @Override
    public @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Object[] input) {
        return new Spell.Event(Spell.Event.TICK_ID, NbtLong.of(ServerUtils.getTime() + 10)); // TODO: Variable time
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Spell.Node node, @Nullable Void context) {
        return spell.getInput(node);
    }
    
}
