package astavie.spellcrafting;

import astavie.spellcrafting.api.spell.Focus;
import astavie.spellcrafting.api.spell.Spell;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ItemTestSpell extends Item {

    public final NbtCompound spell;

    public ItemTestSpell(NbtCompound spell) {
        super(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.OFFHAND).maxCount(1));
        this.spell = spell;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        // TODO: This doesn't work on dedicated servers
        return false;
    }
    
    @Override
    @SuppressWarnings("resource")
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        // Magic!
        if (user.isSneaking()) {
            if (!world.isClient) {
                Pair<Spell, Focus> pair = Spellcrafting.getSpell(user);
                if (pair != null) pair.getLeft().end();
            }
        } else if (world.isClient) {
            HitResult hit = MinecraftClient.getInstance().crosshairTarget;
            Spellcrafting.cast(hit);
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

}
