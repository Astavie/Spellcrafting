package astavie.spellcrafting.block.entity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.spell.node.NodeType;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import astavie.spellcrafting.block.MagicBlock;
import astavie.spellcrafting.block.MagicCircleBlock;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MagicCircleBlockEntity extends BlockEntity {

    private static class SpellException extends RuntimeException {

        public SpellException(String message) {
            super(message);
        }

    }

    private ItemStack[] stacks;

    public MagicCircleBlockEntity(BlockPos pos, BlockState state) {
        super(Spellcrafting.magicCircleBlockEntity, pos, state);
        int size = ((MagicCircleBlock) state.getBlock()).size;
        stacks = new ItemStack[size * size];
        Arrays.fill(stacks, ItemStack.EMPTY);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        NbtList items = new NbtList();
        for (ItemStack stack : stacks) {
            items.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("items", items);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        NbtList items = nbt.getList("items", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < items.size(); i++) {
            stacks[i] = ItemStack.fromNbt(items.getCompound(i));
        }
    }

    public ItemStack getItem(int i) {
        return stacks[i];
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbt = new NbtCompound();
        NbtList items = new NbtList();
        for (ItemStack stack : stacks) {
            items.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put("items", items);
        return nbt;
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    private NodeType getNodeType() {
        // TODO: Better recipes
        return RECIPES.get(ItemVariant.of(stacks[0]));
    }

    public void setItem(int i, ItemStack itemStack) {
        stacks[i] = itemStack;
        markDirty();
        ((ServerWorld) world).getChunkManager().markForUpdate(pos);

        if (itemStack.getItem() == Spellcrafting.spell && itemStack.getSubNbt("spellcrafting:spell") == null) {
            createSpell();
        }
    }

    private static final Map<ItemVariant, NodeType> RECIPES = new HashMap<>();

    static {
        RECIPES.put(ItemVariant.of(Spellcrafting.spell), NodeType.REGISTRY.get(new Identifier("spellcrafting:cast")));
        RECIPES.put(ItemVariant.of(Items.CONDUIT), NodeType.REGISTRY.get(new Identifier("spellcrafting:beam")));
        RECIPES.put(ItemVariant.of(Items.GUNPOWDER), NodeType.REGISTRY.get(new Identifier("spellcrafting:explode")));
        RECIPES.put(ItemVariant.of(Items.ARROW), NodeType.REGISTRY.get(new Identifier("spellcrafting:arrow")));
        RECIPES.put(ItemVariant.of(Items.STRING), NodeType.REGISTRY.get(new Identifier("spellcrafting:cat")));
        RECIPES.put(ItemVariant.of(Items.CLOCK), NodeType.REGISTRY.get(new Identifier("spellcrafting:wait")));
        RECIPES.put(ItemVariant.of(Items.BLAZE_POWDER), NodeType.REGISTRY.get(new Identifier("spellcrafting:ignite")));
        RECIPES.put(ItemVariant.of(Items.ARROW), NodeType.REGISTRY.get(new Identifier("spellcrafting:arrow")));
        RECIPES.put(ItemVariant.of(Items.TARGET), NodeType.REGISTRY.get(new Identifier("spellcrafting:target")));
        RECIPES.put(ItemVariant.of(Items.PISTON), NodeType.REGISTRY.get(new Identifier("spellcrafting:launch")));
        RECIPES.put(ItemVariant.of(Items.FEATHER), NodeType.REGISTRY.get(new Identifier("spellcrafting:land")));
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

        // TODO: Better recipes
        Spell spell = new Spell(start, nodes);
        stacks[0].setSubNbt("spellcrafting:spell", Spell.serialize(spell));
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

        for (BlockPos output : outputs) {
            continueSpell(all, newBlackList, start, nodes, new Spell.Socket(node, 0), world, output.offset(facing), facing.getOpposite());
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
