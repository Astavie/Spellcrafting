package astavie.spellcrafting;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class ItemTestSpell extends Item {

    public ItemTestSpell() {
        super(new FabricItemSettings().equipmentSlot(stack -> EquipmentSlot.OFFHAND).maxCount(1));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }
    
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        TypedActionResult<ItemStack> result = TypedActionResult.pass(stack);

        if (hand != Hand.OFF_HAND) {
            return result;
        }

        ItemStack focus = user.getMainHandStack();
        // TODO: Custom foci
        if (!focus.isEmpty()) {
            return result;
        }

        // Magic!
        if (world.isClient) {
            HitResult hit = MinecraftClient.getInstance().crosshairTarget;
            Spellcrafting.cast(hit);
        }

        user.swingHand(Hand.MAIN_HAND);
        return result;
    }

}
