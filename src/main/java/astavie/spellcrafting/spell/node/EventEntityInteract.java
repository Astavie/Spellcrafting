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
import net.minecraft.util.Identifier;

public class EventEntityInteract implements NodeEvent<Target> {

    private final Identifier eventType;

    public EventEntityInteract(Identifier eventType) {
        this.eventType = eventType;
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public @NotNull SpellType<?>[] getParameters() {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Object[] input) {
        if (!(input[0] instanceof DistancedTarget) || !(((DistancedTarget) input[0]).getTarget() instanceof TargetEntity)) return null;
        
        Entity entity = ((TargetEntity) ((DistancedTarget) input[0]).getTarget()).getEntity();
        return new Spell.Event(eventType, NbtString.of(entity.getUuidAsString()));
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Spell.Node node, Target context) {
        DistancedTarget input = (DistancedTarget) spell.getInput(node)[0];
        if (input == null) {
            return new Object[] {
                null,
                new DistancedTarget(context, null)
            };
        } else {
            return new Object[] {
                input,
                new DistancedTarget(context, input.getOrigin())
            };
        }
    }
    
}
