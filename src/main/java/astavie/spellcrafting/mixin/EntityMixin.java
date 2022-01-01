package astavie.spellcrafting.mixin;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.target.Target;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public class EntityMixin implements Attunable {
    
    private Target attuned;

    @Override
    public boolean isAttunedTo(@NotNull Caster caster) {
        Caster self = Caster.ENTITY_CASTER.find((Entity) (Object) this, null);
        return caster.equals(self) || (attuned != null && attuned.asCaster().equals(caster));
    }

    @Override
    public void attuneTo(@Nullable Caster caster) {
        attuned = caster == null ? null : caster.asTarget();
    }

    @Override
    public @NotNull Vec3d getCenter() {
        return ((Entity) (Object) this).getEyePos();
    }

}
