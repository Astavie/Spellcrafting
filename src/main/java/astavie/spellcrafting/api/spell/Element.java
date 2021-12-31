package astavie.spellcrafting.api.spell;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Element<F, T> extends SpellComponent {

    @NotNull Class<F> getOriginType();

    @NotNull Class<T> getTargetType();
    
    @Nullable T transform(@Nullable F origin);

}
