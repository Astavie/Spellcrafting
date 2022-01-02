package astavie.spellcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(Entity.class)
public class EntityMixin {

    // TODO: Move this to onLandedUpon
    
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V"), method = "fall")
    public void move(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo info) {
        Entity e = (Entity) (Object) this;
        if (e.world.isClient) return;

        Target block = new TargetBlock(e.world, landedPosition, e.getPos(), Direction.UP);

        // TODO: Only works with test spell
        Spellcrafting.activeSpells.forEach(s -> s.onEvent(new Spell.Event(Spell.Event.LAND_ID, NbtString.of(e.getUuidAsString())), block));
    }

}
