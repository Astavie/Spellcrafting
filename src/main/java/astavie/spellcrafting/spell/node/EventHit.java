package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeEvent;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtString;

public class EventHit implements NodeEvent<Target> {

    @Override
    public @NotNull ItemList components() {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public @NotNull SpellType[] eventParameters() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType[] eventReturnTypes() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @Nullable Spell.Event<?> getEvent(@NotNull Spell spell, @NotNull Object[] input) {
        Entity entity = ((Target) input[0]).getEntity();
        if (entity == null) return null;

        return new Spell.Event<>(Spell.Event.HIT_ID, NbtString.of(entity.getUuidAsString()));
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Object[] input, Target context) {
        return new Object[] { context };
    }
    
}
