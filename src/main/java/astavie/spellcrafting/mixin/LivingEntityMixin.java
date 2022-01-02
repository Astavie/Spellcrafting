package astavie.spellcrafting.mixin;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.api.spell.Attunable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements Attunable {

    private UUID attunement = UUID.randomUUID();

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putUuid("spellcrafting:attunement", attunement);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("spellcrafting:attunement")) {
            attunement = nbt.getUuid("spellcrafting:attunement");
        }
    }

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
