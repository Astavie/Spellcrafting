package astavie.spellcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.spell.SpellState;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.HitResult;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Inject(at = @At("HEAD"), method = "onCollision")
    private void onCollision(HitResult result, CallbackInfo callbackInfo) {
        ProjectileEntity entity = (ProjectileEntity) (Object) this;
        if (entity.world.isClient || result.getType() == HitResult.Type.MISS) return;

        Target hit = Spellcrafting.getTarget((ServerWorld) entity.world, result);

        SpellState.getInstance().onEvent(new Spell.Event(Spell.Event.HIT_ID, NbtHelper.fromUuid(entity.getUuid())), hit);
    }
    
}
