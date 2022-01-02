package astavie.spellcrafting.api.spell.node;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import astavie.spellcrafting.api.util.ItemList;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public interface NodeType {

    public static final Registry<NodeType> REGISTRY = FabricRegistryBuilder.createSimple(NodeType.class, new Identifier("spellcrafting:node")).buildAndRegister();

    @NotNull SpellType<?>[] getParameters();

    @NotNull SpellType<?>[] getReturnTypes();

    @NotNull ItemList getComponents();

    default int getComponentFactor(int ouputIndex) {
        return 1;
    }

    void apply(@NotNull Spell spell, @NotNull Spell.Node node, boolean timeSent);

    default void onEvent(@NotNull Spell spell, @NotNull Spell.Node node, Spell.Event type, Object context) {
    }
    
}
