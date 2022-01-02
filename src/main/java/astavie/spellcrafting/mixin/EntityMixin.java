package astavie.spellcrafting.mixin;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtString;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

@Mixin(Entity.class)
public class EntityMixin implements Attunable {

    private UUID attunement = UUID.randomUUID();

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;onEntityLand"), method = "move")
    public void move(MovementType movementType, Vec3d movement, CallbackInfo info) {
        Entity e = (Entity) (Object) this;
        if (e.world.isClient) return;

        Target block = new TargetBlock(e.world, e.getLandingPos(), e.getPos(), Direction.UP);

        // TODO: Only works with test spell
        Spellcrafting.TEST_SPELL.onEvent(new Spell.Event(Spell.Event.LAND_ID, NbtString.of(e.getUuidAsString())), block);
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
