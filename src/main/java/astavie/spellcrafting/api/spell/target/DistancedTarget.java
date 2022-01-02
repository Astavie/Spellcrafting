package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DistancedTarget {

    private final Target target, origin;

    public DistancedTarget(@NotNull Target target, @Nullable Target origin) {
        this.target = target;
        this.origin = origin;
    }

    @NotNull
    public Target getTarget() {
        return target;
    }

    @Nullable
    public Target getOrigin() {
        return origin == null || !origin.exists() ? null : origin;
    }

}
