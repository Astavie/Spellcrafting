package astavie.spellcrafting.spell.node.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtByte;

public class EventCast implements NodeType {

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType[0];
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList();
    }

    @Override
    public void onOn(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        spell.registerEvent(new Spell.Event(Spell.Event.SELF_ID, NbtByte.of((byte) node.channel().ordinal())), node);
    }

    @Override
    public void onOff(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Spell.Event type, @Nullable Object context) {
        spell.apply(node, new Object[] { new DistancedTarget((Target) context, (Target) context, (Target) context) });
    }

    @Override
    public boolean matches(int size, ItemList recipe, EntityType<?> sacrifice) {
        return recipe.size() == 1 && recipe.get(Spellcrafting.spell) == 1;
    }

}
