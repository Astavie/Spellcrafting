package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;

public class ActiveSpell {

    private final Caster caster;
    private final Target target;

    private final Spell spell;

    ActiveSpell(Spell spell, Caster caster, Target target) {
        this.spell = spell;
        this.caster = caster;
        this.target = target;
    }

    public boolean inRange(@NotNull Target t1) {
        return inRange(t1, caster.asTarget());
    }

    public boolean inRange(@NotNull Target t1, @NotNull Target t2) {
        double range = caster.getRange();
        return t1.getPos().squaredDistanceTo(t2.getPos()) <= range * range;
    }

    public Spell getSpell() {
        return spell;
    }

    public Caster getCaster() {
        return caster;
    }

    public Target getTarget() {
        return target;
    }

    public void cast() {
        spell.getSubSpell().cast(this, 0);
    }

    public boolean isDone() {
        return true;
    }

}
