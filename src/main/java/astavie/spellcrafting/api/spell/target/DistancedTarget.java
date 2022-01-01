package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DistancedTarget(@NotNull Target target, @Nullable Target origin) {
}
