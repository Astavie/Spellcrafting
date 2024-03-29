package astavie.spellcrafting.spell;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Attunable;
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
        // TODO: instead of getEyePos use hand position
        return new TargetEntity(player, player.getEyePos());
    }

    @Override
    public boolean isCreative() {
        return player.isCreative();
    }

    @Override
    public @NotNull Storage<ItemVariant> getComponentStorage() {
        return PlayerInventoryStorage.of(player);
    }

    @Override
    public @NotNull Attunable asAttunable() {
        return Attunable.ENTITY_ATTUNABLE.find(player, null);
    }

    @Override
    public double getRange() {
        return player.isCreative() ? 5.0 : 4.5;
    }

    @Override
    public @NotNull UUID getUUID() {
        return player.getUuid();
    }
    
}
