package astavie.spellcrafting.block.entity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.block.MagicBlock;
import astavie.spellcrafting.block.MagicCircleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class MagicCircleBlockEntity extends BlockEntity {

    private static class SpellException extends RuntimeException {

        public SpellException(String message) {
            super(message);
        }

    }

    private final LinkedList<ItemStack> stacks = new LinkedList<>();
    private final int squaredSize;
    private EntityType<?> sacrifice;

    public MagicCircleBlockEntity(BlockPos pos, BlockState state) {
        super(Spellcrafting.magicCircleBlockEntity, pos, state);
        int size = ((MagicCircleBlock) state.getBlock()).size;
        squaredSize = size * size;
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList items = new NbtList();
        for (ItemStack stack : stacks) {
            items.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("items", items);
        if (sacrifice != null) nbt.putString("sacrifice", Registry.ENTITY_TYPE.getKey(sacrifice).get().getValue().toString());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        stacks.clear();
        sacrifice = null;

        NbtList items = nbt.getList("items", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < items.size(); i++) {
            stacks.add(ItemStack.fromNbt(items.getCompound(i)));
        }

        if (nbt.contains("sacrifice")) sacrifice = Registry.ENTITY_TYPE.get(new Identifier(nbt.getString("sacrifice")));
    }

    public ItemStack peekItem() {
        return stacks.size() == 0 ? ItemStack.EMPTY : stacks.getLast();
    }

    public ItemStack getItem(int i) {
        return i >= stacks.size() ? ItemStack.EMPTY : stacks.get(i);
    }

    public ItemStack popItem() {
        if (stacks.size() == 0) return ItemStack.EMPTY;

        markDirty();
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);

        return stacks.removeLast();
    }

    public boolean pushItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof SpawnEggItem) {
            setSacrifice(((SpawnEggItem) itemStack.getItem()).getEntityType(itemStack.getNbt()));
            return true;
        }

        if (stacks.size() >= squaredSize) return false;

        stacks.add(itemStack);
        markDirty();
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);

        if (itemStack.getItem() == Spellcrafting.spell && itemStack.getSubNbt("spellcrafting:spell") == null) {
            createSpell();
        }

        return true;
    }

    public void setSacrifice(EntityType<?> type) {
        this.sacrifice = type;

        markDirty();
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);
    }

    public EntityType<?> getSacrifice() {
        return this.sacrifice;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtList items = new NbtList();
        for (ItemStack stack : stacks) {
            items.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("items", items);
        if (sacrifice != null) nbt.putString("sacrifice", Registry.ENTITY_TYPE.getKey(sacrifice).get().getValue().toString());
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    private NodeType getNodeType() {
        ItemList items = new ItemList();
        for (ItemStack stack : stacks) {
            items.addItem(stack);
        }

        int size = ((MagicCircleBlock) getCachedState().getBlock()).size;

        for (var entry : NodeType.REGISTRY.getEntries()) {
            if (entry.getValue().matches(size, items, sacrifice)) return entry.getValue();
        }

        return null;
    }

    private BlockPos[] getInputPositions() {
        BlockState state = getCachedState();
        MagicCircleBlock block = (MagicCircleBlock) state.getBlock();

        Direction right = state.get(MagicCircleBlock.FACING);
        Direction down = right.rotateYClockwise();

        BlockPos[] inputPositions = new BlockPos[block.size];

        for (int i = 0; i < block.size; i++) {
            BlockPos p = pos.offset(down, i);
            inputPositions[i] = p;
        }

        return inputPositions;
    }

    private BlockPos[] getOutputPositions() {
        BlockState state = getCachedState();
        MagicCircleBlock block = (MagicCircleBlock) state.getBlock();

        Direction right = state.get(MagicCircleBlock.FACING);
        Direction down = right.rotateYClockwise();

        BlockPos[] inputPositions = new BlockPos[block.size];

        for (int i = 0; i < block.size; i++) {
            BlockPos p = pos.offset(right, block.size - 1).offset(down, i);
            inputPositions[i] = p;
        }

        return inputPositions;
    }

    private void createSpell() {
        Set<Spell.Node> start = new HashSet<>();
        Multimap<Spell.Socket, Spell.Socket> nodes = HashMultimap.create();

        try {
            continueSpell(new HashMap<>(), new HashSet<>(), start, nodes, null, null);
        } catch (SpellException e) {
            System.out.println("Failed to create spell from circle: " + e.getMessage());
            return;
        }

        Spell spell = new Spell(start, nodes);
        stacks.getFirst().setSubNbt("spellcrafting:spell", Spell.serialize(spell));
    }

    private void continueSpell(Map<BlockPos, Spell.Node> all, Set<Spell.Node> blacklist, Set<Spell.Node> start, Multimap<Spell.Socket, Spell.Socket> nodes, BlockPos pos, Spell.Socket previous) {
        NodeType type = getNodeType();
        if (type == null) throw new SpellException("No recipe: " + this.pos);

        if (all.containsKey(pos)) {
            // We already visited this node
            if (blacklist.contains(all.get(pos))) {
                throw new SpellException("Circular dependency: " + this.pos);
            }

            BlockPos[] inputs = getInputPositions();
            for (int i = 0; i < inputs.length; i++) {
                if (inputs[i].equals(pos)) {
                    nodes.put(previous, new Spell.Socket(all.get(pos), i));
                    return;
                }
            }
            return;
        }

        // New node!
        Spell.Node node = new Spell.Node(type, ((MagicCircleBlock) getCachedState().getBlock()).size);
        BlockPos[] inputs = getInputPositions();
        int index = 0;
        for (int i = 0; i < inputs.length; i++) {
            all.put(inputs[i], node);
            if (inputs[i].equals(pos)) {
                index = i;
            }
        }

        if (type.getParameters(node).length == 0) {
            start.add(node);
        } else if (previous != null) {
            nodes.put(previous, new Spell.Socket(node, index));
        }

        BlockPos[] outputs = getOutputPositions();
        Direction facing = getCachedState().get(MagicCircleBlock.FACING);

        Set<Spell.Node> newBlackList = new HashSet<>(blacklist);
        newBlackList.add(node);

        for (int i = 0; i < outputs.length; i++) {
            continueSpell(all, newBlackList, start, nodes, new Spell.Socket(node, i), world, outputs[i].offset(facing), facing.getOpposite());
        }
    }

    private static void continueSpell(Map<BlockPos, Spell.Node> all, Set<Spell.Node> blacklist, Set<Spell.Node> start, Multimap<Spell.Socket, Spell.Socket> nodes, Spell.Socket previous, World world, BlockPos pos, Direction input) {
        // TODO: These should work with the block lookup api

        BlockState block = world.getBlockState(pos);
        if (!(block.getBlock() instanceof MagicBlock)) return;

        MagicBlock magic = (MagicBlock) block.getBlock();
        if (!magic.isInput(world, pos, block, input)) return;

        if (block.getBlock() instanceof MagicCircleBlock) {
            MagicCircleBlockEntity be = ((MagicCircleBlock) block.getBlock()).getBlockEntity(world, pos);
            if (be != null) {
                be.continueSpell(all, blacklist, start, nodes, pos, previous);
            }
            return;
        }

        for (Direction direction : magic.getOutputs(world, pos, block)) {
            continueSpell(all, blacklist, start, nodes, previous, world, pos.offset(direction), direction.getOpposite());
        }
    }
    
}
