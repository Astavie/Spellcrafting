package astavie.spellcrafting.spell;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.spell.target.TargetEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;

public record CasterPlayer(PlayerEntity player) implements Caster {

    @Override
    public @NotNull Target asTarget() {
        // TODO: instead of getEyePos use hand position for pos
        return new TargetEntity(player, player.getEyePos(), player.getEyePos());
    }

    @Override
    public @NotNull Storage<ItemVariant> getComponentStorage() {
        return PlayerInventoryStorage.of(player);
    }

    @Override
    public double getRange() {
        return player.isCreative() ? 5.0 : 4.5;
    }
    
}
