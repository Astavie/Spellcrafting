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
import net.minecraft.nbt.NbtHelper;
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
        Target block = new TargetBlock((ServerWorld) e.world, landedPosition, e.getPos(), Direction.UP);

        SpellState.getInstance().onEvent(new Spell.Event(Spell.Event.LAND_ID, NbtHelper.fromUuid(e.getUuid())), block);
    }

    @Inject(method = "readNbt", at = @At("TAIL"))
    private void readNbt(NbtCompound nbt, CallbackInfo ci) {
        prevOnGround = ((Entity) (Object) this).isOnGround();
    }

}
