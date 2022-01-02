package astavie.spellcrafting.api.spell.node;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.util.ItemList;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface NodeType {

    public static final Registry<NodeType> REGISTRY = FabricRegistryBuilder.createSimple(NodeType.class, new Identifier("spellcrafting:node")).buildAndRegister();

    @NotNull SpellType<?>[] getParameters();

    @NotNull SpellType<?>[] getReturnTypes(@NotNull Spell spell, @NotNull Spell.Node node);

    @NotNull ItemList getComponents(@NotNull Spell spell, @NotNull Spell.Node node);

    // TODO: Go back to the Object[] return type and do breadth first instead of depth first
    void apply(@NotNull Spell spell, @NotNull Spell.Node node);

    default void onEvent(@NotNull Spell spell, @NotNull Spell.Node node, @NotNull Spell.Event type, @Nullable Object context) {
    }
    
}
