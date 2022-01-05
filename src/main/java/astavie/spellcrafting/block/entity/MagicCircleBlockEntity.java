package astavie.spellcrafting.block.entity;

import astavie.spellcrafting.Spellcrafting;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public class MagicCircleBlockEntity extends BlockEntity {

    private ItemStack item = ItemStack.EMPTY;

    public MagicCircleBlockEntity(BlockPos pos, BlockState state) {
        super(Spellcrafting.magicCircleBlockEntity, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        nbt.put("item", item.writeNbt(new NbtCompound()));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        item = ItemStack.fromNbt(nbt.getCompound("item"));
    }

    public ItemStack getItem() {
        return item;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("item", item.writeNbt(new NbtCompound()));
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void setItem(ItemStack itemStack) {
        item = itemStack;
        markDirty();
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);
    }
    
}
