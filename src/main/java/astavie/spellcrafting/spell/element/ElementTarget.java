package astavie.spellcrafting.spell.element;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.ActiveSpell;
import astavie.spellcrafting.api.spell.Element;
import astavie.spellcrafting.api.spell.Target;
import net.minecraft.util.Identifier;

public class ElementTarget implements Element<ActiveSpell, Target> {

    @Override
    public Identifier getIdentifier() {
        return new Identifier("spellcrafting:target");
    }

    @Override
    public @NotNull Class<ActiveSpell> getOriginType() {
        return ActiveSpell.class;
    }

    @Override
    public @NotNull Class<Target> getTargetType() {
        return Target.class;
    }

    @Override
    public @Nullable Target transform(@Nullable ActiveSpell origin) {
        return origin == null ? null : origin.getTarget();
    }
    
}
