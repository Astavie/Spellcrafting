package astavie.spellcrafting.spell.node.event;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.minecraft.nbt.NbtHelper;

public class EventTarget implements NodeType {

    // TODO: Targets of other entities

    @Override
    public @NotNull SpellType<?>[] getParameters(@NotNull Spell.Node node) {
        return new SpellType[] { SpellType.TARGET };
    }

    @Override
    public @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node) {
        SpellType<?>[] types = new SpellType<?>[node.getSize()];
        for (int i = 0; i < node.getSize(); i++) types[i] = SpellType.TARGET;
        return types;
    }

    @Override
    public @NotNull ItemList getComponents(@NotNull Spell.Node node) {
        return new ItemList(); // TODO: Components?
    }

    @Override
    public void onOn(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        DistancedTarget target = (DistancedTarget) spell.getInput(node)[0];
        if (target != null && target.getTarget().exists()) {
            Caster caster = target.getTarget().asCaster();
            if (caster != null) {
               spell.registerEvent(new Spell.Event(Spell.Event.TARGET_ID, NbtHelper.fromUuid(caster.getUUID())), node);
               spell.apply(node, new Object[node.node().getSize()]);
               return;
            }
        }

        spell.cancelEvents(node);
        spell.apply(node, new Object[node.node().getSize()]);
    }

    @Override
    public void onOff(@NotNull Spell spell, @NotNull Spell.ChannelNode node) {
        spell.cancelEvents(node);
        spell.apply(node, new Object[node.node().getSize()]);
    }

    @Override
    public void onEvent(@NotNull Spell spell, @NotNull Spell.ChannelNode node, @NotNull Spell.Event type, @Nullable Object context) {
        DistancedTarget caster = (DistancedTarget) spell.getInput(node)[0];
        if (caster == null || caster.getTarget().asCaster() == null) return;

        for (int i = 0; i < node.node().getSize(); i++) {
            Spell.ChannelSocket socket = new Spell.ChannelSocket(node.node(), i, node.channel());
            if (spell.getOutput(socket) != null) continue;

            spell.apply(socket, new DistancedTarget((Target) context, caster.getOrigin(), caster.getCaster()));
            if (i < node.node().getSize() - 1) {
                spell.registerEvent(new Spell.Event(Spell.Event.TARGET_ID, NbtHelper.fromUuid(caster.getTarget().asCaster().getUUID())), node);
            }
            return;
        }
    }
    
}
