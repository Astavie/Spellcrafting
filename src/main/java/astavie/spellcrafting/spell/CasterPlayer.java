package astavie.spellcrafting.spell;

import org.jetbrains.annotations.NotNull;

import astavie.spellcrafting.api.spell.Caster;
import astavie.spellcrafting.api.spell.Target;
import astavie.spellcrafting.api.spell.TargetEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.entity.player.PlayerEntity;

public record CasterPlayer(@NotNull PlayerEntity player) implements Caster {
    
    @Override
    public Target asTarget() {
        return new TargetEntity(player, player.getEyePos());
    }

    @Override
    public Storage<ItemVariant> getComponentStorage() {
        // TODO: Component pouches
        return PlayerInventoryStorage.of(player);
    }

    @Override
    public double getRange() {
        return player.isCreative() ? 5.0f : 4.5f;
    }
    
}
