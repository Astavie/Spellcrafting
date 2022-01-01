package astavie.spellcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.target.Target;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.hit.HitResult;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(at = @At("HEAD"), method = "onCollision")
    private void onCollision(HitResult result, CallbackInfo callbackInfo) {
        ProjectileEntity entity = (ProjectileEntity) (Object) this;

        if (entity.world.isClient || result.getType() == HitResult.Type.MISS) return;

        Target hit = Spellcrafting.getTarget(entity.world, entity.getPos(), result);

        // TODO: Check if attuned

        // TODO: This now only works on test spell
        Spellcrafting.TEST_SPELL.onEvent(new Spell.Event<>(Spell.Event.HIT_ID, NbtString.of(entity.getUuidAsString())), hit);
    }
    
}
