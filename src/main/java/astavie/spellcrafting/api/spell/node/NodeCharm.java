package astavie.spellcrafting.api.spell.node;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.SpellType;
import net.minecraft.util.Unit;

public interface NodeCharm extends NodeType {

    @Override
    default @NotNull SpellType<?>[] getParameters() {
        return ArrayUtils.insert(0, getCharmParameters(), SpellType.TIME);
    }

    @Override
    default @NotNull SpellType<?>[] getReturnTypes() {
        return ArrayUtils.insert(0, getCharmReturnTypes(), SpellType.TIME);
    }

    @Override
    default void apply(@NotNull Spell spell, @NotNull Spell.Node node, boolean timeSent) {
        if (!timeSent) return;
        
        Object[] input = spell.getInput(node);
        if (input[0] != null) {
            spell.apply(node, ArrayUtils.insert(0, cast(spell, Arrays.copyOfRange(input, 1, input.length)), (Unit) null));
            spell.schedule(node);
        }
    }

    @Override
    default void onEvent(@NotNull Spell spell, @NotNull Spell.Node node, Spell.Event type, Object context) {
        spell.apply(node, 0, Unit.INSTANCE);
    }

    @NotNull SpellType<?>[] getCharmParameters();
    
    @NotNull SpellType<?>[] getCharmReturnTypes();

    @NotNull Object[] cast(@NotNull Spell spell, @NotNull Object[] input);
    
}
