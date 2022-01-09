package astavie.spellcrafting.spell.node.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeEvent;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import astavie.spellcrafting.api.util.ItemList;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.Identifier;

public class EventEntityInteract implements NodeEvent<Target> {

    private final Identifier eventType;
    private final ItemList components;
    private final ItemVariant recipe;

    public EventEntityInteract(Identifier eventType, ItemList components, ItemVariant recipe) {
        this.eventType = eventType;
        this.components = components;
        this.recipe = recipe;
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return components; // TODO: Components?
    }

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET, SpellType.TARGET };
    }

    @Override
    public @Nullable Spell.Event getEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Object[] input) {
        if (!(input[0] instanceof DistancedTarget) || !(((DistancedTarget) input[0]).getTarget() instanceof TargetEntity) || !((DistancedTarget) input[0]).getTarget().exists()) return null;
        
        Entity entity = ((TargetEntity) ((DistancedTarget) input[0]).getTarget()).getEntity();
        return new Spell.Event(eventType, NbtHelper.fromUuid(entity.getUuid()));
    }

    @Override
    public @NotNull Object[] onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, Target context) {
        DistancedTarget input = (DistancedTarget) spell.getInput(node)[0];
        if (input == null) {
            return new Object[] {
                null,
                null
            };
        } else {
            return new Object[] {
                input,
                new DistancedTarget(context, input.getOrigin(), input.getCaster())
            };
        }
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return recipe.size() == 1 && recipe.get(this.recipe) == 1;
    }
    
}
