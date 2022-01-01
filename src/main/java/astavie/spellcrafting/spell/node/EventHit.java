package astavie.spellcrafting.spell.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeEvent;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtString;

public class EventHit implements NodeEvent<Target> {

    @Override
    public @NotNull ItemList getComponents() {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public @NotNull SpellType[] getEventParameters() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType[] getEventReturnTypes() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Object[] input) {
        if (!(input[0] instanceof DistancedTarget) || !(((DistancedTarget) input[0]).target() instanceof TargetEntity)) return null;
        
        Entity entity = ((TargetEntity) ((DistancedTarget) input[0]).target()).getEntity();
        return new Spell.Event(Spell.Event.HIT_ID, NbtString.of(entity.getUuidAsString()));
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, Target context) {
        DistancedTarget input = (DistancedTarget) spell.getInput(this)[1];
        if (input == null) {
            return new Object[] { new DistancedTarget(context, null) };
        } else {
            return new Object[] { new DistancedTarget(context, input.origin()) };
        }
    }
    
}
