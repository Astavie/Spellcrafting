package astavie.spellcrafting.mixin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import astavie.spellcrafting.api.spell.Attunable;
import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements Caster {

    private final Map<UUID, Spell> spells = new HashMap<>();

    @Override
    public @NotNull Target asTarget() {
        PlayerEntity player = (PlayerEntity) (Object) this;

        // TODO: instead of getEyePos use hand position
        return new TargetEntity(player, player.getEyePos());
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList spells = new NbtList();
        for (Spell spell : this.spells.values()) {
            if (!spell.isActive()) continue;
            spells.add(Spell.serialize(spell));
        }
        nbt.put("spellcrafting:spells", spells);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList spells = nbt.getList("spellcrafting:spells", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < spells.size(); i++) {
            Spell spell = Spell.deserialize(spells.getCompound(i), (ServerWorld) (((PlayerEntity) (Object) this).world));
            spell.activate(this);
            addSpell(spell);
        }
    }

    @Override
    public @NotNull Storage<ItemVariant> getComponentStorage() {
        return PlayerInventoryStorage.of((PlayerEntity) (Object) this);
    }

    @Override
    public @NotNull Attunable asAttunable() {
        return Attunable.ENTITY_ATTUNABLE.find((PlayerEntity) (Object) this, null);
    }

    @Override
    public double getRange() {
        return ((PlayerEntity) (Object) this).isCreative() ? 5.0 : 4.5;
    }

    @Override
    public @Nullable Spell getSpell(@NotNull UUID uuid) {
        return spells.get(uuid);
    }

    @Override
    public void addSpell(@NotNull Spell spell) {
        spells.put(spell.getUUID(), spell);
    }

    @Override
    public void removeSpell(@NotNull UUID uuid) {
        spells.remove(uuid);
    }

    @Override
    public boolean exists() {
        return !((PlayerEntity) (Object) this).isRemoved();
    }

}
