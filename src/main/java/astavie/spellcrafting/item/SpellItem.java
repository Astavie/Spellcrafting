package astavie.spellcrafting.item;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Focus;
import astavie.spellcrafting.api.spell.Spell;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SpellItem extends Item {

    public SpellItem() {
        super(new FabricItemSettings().group(ItemGroup.MISC).rarity(Rarity.RARE).equipmentSlot(stack -> EquipmentSlot.OFFHAND).maxCount(1));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getSubNbt("spellcrafting:spell") != null;
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        if (isIn(group)) {
            stacks.add(getStack(Spellcrafting.BOMB, "Bomb"));
            stacks.add(getStack(Spellcrafting.EXPLODING_KITTENS, "Exploding Kittens"));
            stacks.add(getStack(Spellcrafting.FIREWORK, "Firework"));
            stacks.add(getStack(Spellcrafting.ATTRACTIVE_CAT, "Attractive Cat"));
            stacks.add(getStack(Spellcrafting.ARROW_STORM, "Arrow Storm"));
        }
    }

    private ItemStack getStack(Spell spell, String name) {
        ItemStack stack = new ItemStack(this);
        stack.setSubNbt("spellcrafting:spell", Spell.serialize(spell));
        stack.setCustomName(new LiteralText(name).setStyle(Style.EMPTY.withItalic(false)));
        return stack;
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
