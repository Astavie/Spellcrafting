package astavie.spellcrafting.api.spell;

public class Spell {

    private final SubSpell subspell;

    public Spell(SubSpell subspell) {
        this.subspell = subspell;
    }

    public SubSpell getSubSpell() {
        return subspell;
    }

    public ActiveSpell activate(Caster caster, Target target) {
        return new ActiveSpell(this, caster, target);
    }
    
}
