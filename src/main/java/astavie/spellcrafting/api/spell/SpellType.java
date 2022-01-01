package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.target.DistancedTarget;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;

public record SpellType(@NotNull DyeColor color, @NotNull Class<?> valueType) {

    public static final @NotNull SpellType TIME = new SpellType(DyeColor.WHITE, Unit.class);
    public static final @NotNull SpellType TARGET = new SpellType(DyeColor.BLUE, DistancedTarget.class);

}
