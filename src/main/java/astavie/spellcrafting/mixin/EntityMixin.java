package astavie.spellcrafting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import astavie.spellcrafting.spell.SpellState;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(Entity.class)
public class EntityMixin {

    public boolean prevOnGround = true;
    
    @Inject(at = @At(value = "HEAD"), method = "fall")
    public void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition, CallbackInfo info) {
        Entity e = (Entity) (Object) this;
        if (e.world.isClient || !onGround || prevOnGround) {
            prevOnGround = onGround;
            return;
        }

        prevOnGround = true;
        Target block = new TargetBlock(e.world, landedPosition, e.getPos(), Direction.UP);

        SpellState.of((ServerWorld) e.world).onEvent(new Spell.Event(Spell.Event.LAND_ID, NbtString.of(e.getUuidAsString())), block);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        prevOnGround = ((Entity) (Object) this).isOnGround();
    }

}
