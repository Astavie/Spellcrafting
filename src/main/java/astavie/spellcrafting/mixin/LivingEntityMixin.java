package astavie.spellcrafting.mixin;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import astavie.spellcrafting.api.spell.Attunable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements Attunable {

    // TODO: persist across saves (and player deaths?)

    private UUID attunement = UUID.randomUUID();

    @Override
    public void attuneTo(@Nullable Attunable attunable) {
        attunement = attunable == null ? UUID.randomUUID() : attunable.getAttunement();
    }

    @Override
    public UUID getAttunement() {
        return attunement;
    }

    @Override
    public @NotNull Vec3d getCenter() {
        return ((Entity) (Object) this).getEyePos();
    }
    
}
