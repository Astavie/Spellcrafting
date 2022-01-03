package astavie.spellcrafting.api.spell.target;

import org.jetbrains.annotations.NotNull;

public class DistancedTarget {

    private final Target target, origin, caster;

    public DistancedTarget(@NotNull Target target, @NotNull Target origin, @NotNull Target caster) {
        this.target = target;
        this.origin = origin;
        this.caster = caster;
    }

    @NotNull
    public Target getTarget() {
        return target;
    }

    @NotNull
    public Target getOrigin() {
        return origin;
    }

    @NotNull
    public Target getCaster() {
        return caster;
    }

    public boolean inRange() {
        // Is attuned
        if (
           !caster.exists() ||
            caster.asAttunable() == null ||

           !origin.exists() ||
            origin.asAttunable() == null ||

           !origin.asAttunable().isAttunedTo(caster.asAttunable())
        ) return false;

        // Check range
        double range = caster.asCaster().getRange();
        return origin.getPos().squaredDistanceTo(target.getPos()) <= range * range + 0.1;
    }

}
