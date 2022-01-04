package astavie.spellcrafting.api.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.node.NodeType;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.api.util.ServerUtils;
import astavie.spellcrafting.spell.SpellState;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

// TODO: Turn this into an interface with an Impl
public class Spell {

    public static class Node {
        private final NodeType type;
        private final int size;
        public Node(@NotNull NodeType type, int size) {
            this.type = type;
            this.size = size;
        }
        public Node(@NotNull NodeType type) {
            this(type, 1);
        }
        public int getSize() {
            return size;
        }
    }

    public static record Socket(@NotNull Node node, int index) {
    }

    public static record ChannelSocket(@NotNull Node node, int index, DyeColor channel) {
    }

    public static record ChannelNode(@NotNull Node node, DyeColor channel) {
    }

    public static record Event(@NotNull Identifier eventType, @NotNull NbtElement argument) {

        public static final @NotNull Identifier SELF_ID = new Identifier("spellcrafting:self");
        public static final @NotNull Identifier TARGET_ID = new Identifier("spellcrafting:target");

        public static final @NotNull Identifier HIT_ID = new Identifier("spellcrafting:hit");
        public static final @NotNull Identifier LAND_ID = new Identifier("spellcrafting:land");
        public static final @NotNull Identifier TICK_ID = new Identifier("spellcrafting:tick");
        
    }

    private final Map<Event, Object> eventsThisTick = new HashMap<>();

    private final UUID uuid;
    private final Set<Node> start;
    private final Multimap<Socket, Socket> nodes;
    private final Map<Socket, Socket> inverse = new HashMap<>();
    private final Map<ChannelSocket, Object> output = new HashMap<>();
    private final Multimap<Event, ChannelNode> events = HashMultimap.create();
    private final ItemList components = new ItemList();

    private Spell(UUID uuid) {
        this.start = new HashSet<>();
        this.nodes = HashMultimap.create();
        this.uuid = uuid;
    }

    public Spell(Set<Node> start, Multimap<Socket, Socket> nodes) {
        if (start.stream().anyMatch(n -> n.type.getParameters(n).length > 0)) throw new IllegalArgumentException("Illegal start node");
        this.start = start;
        this.nodes = nodes;
        this.uuid = UUID.randomUUID();
        
        for (Map.Entry<Socket, Socket> entry : nodes.entries()) {
            if (inverse.containsKey(entry.getValue())) throw new IllegalArgumentException("Spell has edges that combine");
            inverse.put(entry.getValue(), entry.getKey());
        }

        calculateComponents();
    }

    public void markDirty() {
        SpellState.getInstance().markDirty();
    }

    private void calculateComponents() {
        // Check graph and calculate multipliers
        Set<Node> nodes = new HashSet<>();
        for (Node node : start) {
            nodes.add(node);
            continueFactor(nodes, new HashSet<>(), node);
        }

        // Add components
        for (Node node : nodes) {
            components.addItemList(node.type.getComponents(node));
        }
    }

    public @Nullable SpellType<?> getActualInputType(@NotNull Socket socket) {
        Socket out = inverse.get(socket);
        if (out == null) return null;
        return out.node.type.getReturnTypes(this, out.node)[out.index];
    }

    public static @NotNull NbtCompound serialize(Spell spell) {
        Map<Node, Integer> nodes = new HashMap<>();
        Map<Socket, Integer> sockets = new HashMap<>();

        Map<ChannelNode, Event> inverseEvents = new HashMap<>();
        for (Map.Entry<Event, ChannelNode> entry : spell.events.entries()) {
            inverseEvents.put(entry.getValue(), entry.getKey());
        }

        NbtList nodeList = new NbtList();
        NbtList socketList = new NbtList();

        // Pass 0: starting nodes
        for (Node node : spell.start) {
            nodes.put(node, nodeList.size());

            NbtCompound cmp = new NbtCompound();
            cmp.putString("type", NodeType.REGISTRY.getId(node.type).toString());
            cmp.putInt("size", node.size);
            nodeList.add(cmp);
        }

        // Pass 0: add all nodes with events
        for (Socket in : spell.inverse.keySet()) {
            if (!nodes.containsKey(in.node)) {
                nodes.put(in.node, nodeList.size());

                NbtCompound cmp = new NbtCompound();
                cmp.putString("type", NodeType.REGISTRY.getId(in.node.type).toString());
                cmp.putInt("size", in.node.size);

                for (DyeColor color : DyeColor.values()) {
                    ChannelNode channel = new ChannelNode(in.node, color);
                    if (inverseEvents.containsKey(channel)) {
                        cmp.putString(color.getName() + "_event", inverseEvents.get(channel).eventType.toString());
                        cmp.put(color.getName() + "_arg", inverseEvents.get(channel).argument);
                    }
                }

                nodeList.add(cmp);
            }
        }

        // Pass 1: add all sockets and their output
        for (Socket out : spell.nodes.keySet()) {

            sockets.put(out, socketList.size());

            NbtCompound cmp = new NbtCompound();
            cmp.putInt("node", nodes.get(out.node));
            cmp.putInt("index", out.index);
            
            for (DyeColor color : DyeColor.values()) {
                ChannelSocket channel = new ChannelSocket(out.node, out.index, color);
                if (spell.output.containsKey(channel)) {
                    SpellType<?> type = out.node.type.getReturnTypes(spell, out.node)[out.index];
                    cmp.put(color.getName() + "_value", SpellType.serialize(type, spell.output.get(channel)));
                }
            }

            socketList.add(cmp);
        }

        // Pass 2: add all node connections
        for (Node node : nodes.keySet()) {
            int parameters = node.type.getParameters(node).length;
            NbtIntArray connections = new NbtIntArray(new int[parameters]);

            for (int i = 0; i < parameters; i++) {
                Socket in = new Socket(node, i);
                Socket out = spell.inverse.get(in);
                if (out == null) {
                    connections.set(i, NbtInt.of(-1));
                } else {
                    connections.set(i, NbtInt.of(sockets.get(out)));
                }
            }

            nodeList.getCompound(nodes.get(node)).put("from", connections);
        }

        NbtCompound cmp = new NbtCompound();
        cmp.putUuid("UUID", spell.uuid);
        cmp.put("nodes", nodeList);
        cmp.put("sockets", socketList);
        return cmp;
    }

    public static Spell deserialize(@NotNull NbtCompound nbt) {
        NbtList nodes = nbt.getList("nodes", NbtElement.COMPOUND_TYPE);
        NbtList sockets = nbt.getList("sockets", NbtElement.COMPOUND_TYPE);

        Node[] totalNodes = new Node[nodes.size()];
        Socket[] totalSockets = new Socket[sockets.size()];

        Spell spell = new Spell(nbt.getUuid("UUID"));

        // Phase 0: get all nodes with events
        for (int i = 0; i < nodes.size(); i++) {
            NbtCompound cmp = nodes.getCompound(i);
            Node node = new Node(NodeType.REGISTRY.get(new Identifier(cmp.getString("type"))), cmp.getInt("size"));
            totalNodes[i] = node;

            if (node.type.getParameters(node).length == 0) {
                spell.start.add(node);
            }

            for (DyeColor color : DyeColor.values()) {
                if (cmp.contains(color.getName() + "_event")) {
                    Event event = new Event(new Identifier(cmp.getString(color.getName() + "_event")), cmp.get(color.getName() + "_arg"));
                    spell.events.put(event, new ChannelNode(node, color));
                }
            }
        }

        // Phase 1: get all sockets
        for (int i = 0; i < sockets.size(); i++) {
            NbtCompound cmp = sockets.getCompound(i);

            Socket socket = new Socket(totalNodes[cmp.getInt("node")], cmp.getInt("index"));
            totalSockets[i] = socket;
        }

        // Phase 2: get all connections
        for (int i = 1; i < nodes.size(); i++) {
            int[] from = nodes.getCompound(i).getIntArray("from");
            Node node = totalNodes[i];
            int parameters = node.type.getParameters(node).length;

            for (int j = 0; j < Math.min(from.length, parameters); j++) {
                if (from[j] == -1) continue;

                Socket in = new Socket(node, j);
                Socket out = totalSockets[from[j]];
                spell.inverse.put(in, out);
                spell.nodes.put(out, in);
            }
        }

        // Phase 3: get all outputs
        for (int i = 0; i < sockets.size(); i++) {
            NbtCompound cmp = sockets.getCompound(i);
            for (DyeColor color : DyeColor.values()) {
                if (cmp.contains(color.getName() + "_value")) {
                    Socket socket = totalSockets[i];
                    SpellType<?> type = socket.node.type.getReturnTypes(spell, socket.node)[socket.index];
                    spell.output.put(new ChannelSocket(socket.node, socket.index, color), type.deserialize(cmp.get(color.getName() + "_value")));
                }
            }
        }

        spell.calculateComponents();
        return spell;
    }

    public void onInvalidPosition(ServerWorld world, Vec3d pos) {
        world.spawnParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 6, 0.2, 0.2, 0.2, 0.01);
    }

    private void continueFactor(Set<Node> total, Set<Node> blacklist, Node node) {
        Set<Node> nextBlacklist = new HashSet<>(blacklist);
        nextBlacklist.add(node);

        int returnTypes = node.type.getReturnTypes(this, node).length;
        for (int i = 0; i < returnTypes; i++) {
            for (Socket in : nodes.get(new Socket(node, i))) {
                if (nextBlacklist.contains(in.node)) throw new IllegalArgumentException("Cyclic spell");
                if (!in.node.type.getParameters(in.node)[in.index].getValueClass().isAssignableFrom(node.type.getReturnTypes(this, node)[i].getValueClass())) throw new IllegalArgumentException("Spell has illegal connections");

                if (!total.contains(in.node)) {
                    total.add(in.node);
                    continueFactor(total, nextBlacklist, in.node);
                }
            }
        }
    }

    public @NotNull UUID getUUID() {
        return uuid;
    }

    public @Nullable Object getOutput(@NotNull ChannelSocket socket) {
        return output.get(socket);
    }

    public @NotNull ItemList components() {
        return components;
    }

    public void end() {
        // TODO: API breach
        // TODO: end single dye color
        SpellState.getInstance().removeSpell(uuid);
        output.clear();
        events.clear();
        eventsThisTick.clear();
    }

    public boolean onTarget(@NotNull Caster caster, @NotNull Target target) {
        Event event = new Event(Event.TARGET_ID, NbtHelper.fromUuid(caster.getUUID()));
        if (events.containsKey(event)) {
            onEvent(event, target);
            return true;
        }

        for (DyeColor color : DyeColor.values()) {
            // Check if any starting nodes already contain output on this channel
            // TODO: This is horrible
            if (start.stream().anyMatch(s -> {
                int returnTypes = s.type.getReturnTypes(this, s).length;
                for (int i = 0; i < returnTypes; i++) {
                    if (output.containsKey(new ChannelSocket(s, i, color))) {
                        return true;
                    }
                }
                return false;
            })) continue;

            // Use components
            Transaction transaction = Transaction.openOuter();
            ItemList missing = caster.useComponents(components, transaction);
            if (!missing.isEmpty()) {
                transaction.abort();
                return false;
            }

            // Cast spell!
            transaction.commit();

            // TODO: API breach
            SpellState.getInstance().addSpell(this);

            for (Node node : start) node.type.apply(this, new ChannelNode(node, color));
            onEvent(new Event(Event.SELF_ID, NbtByte.of((byte) color.ordinal())), caster.asTarget());
            onEvent(event, target);

            return true;
        }

        return false;
    }

    public void registerEvent(@NotNull Event event, @NotNull ChannelNode handler) {
        cancelEvents(handler);
        if (eventsThisTick.containsKey(event)) {
            handler.node.type.onEvent(this, handler, event, eventsThisTick.get(event));
        } else {
            events.put(event, handler);
        }
    }

    public boolean hasEvent(@NotNull ChannelNode handler) {
        return events.values().contains(handler);
    }

    public void cancelEvents(@NotNull ChannelNode handler) {
        events.values().remove(handler);
    }

    public void onEvent(@NotNull Event event, Object context) {
        if (event.eventType == Event.TICK_ID && events.isEmpty()) {
            end();
            return;
        }
        
        for (ChannelNode node : events.removeAll(event)) {
            node.node.type.onEvent(this, node, event, context);
        }
        if (event.eventType == Event.TICK_ID) {
            eventsThisTick.clear();
        } else {
            eventsThisTick.put(event, context);
        }
    }

    public void schedule(@NotNull ChannelNode handler) {
        registerEvent(new Event(Event.TICK_ID, NbtLong.of(ServerUtils.getTime() + 1)), handler);
    }

    public @NotNull Object[] getInput(@NotNull ChannelNode node) {
        SpellType<?>[] parameters = node.node.type.getParameters(node.node);
        Object[] ret = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Socket out = inverse.get(new Socket(node.node, i));
            ret[i] = out == null ? null : output.get(new ChannelSocket(out.node, out.index, node.channel));
            if (ret[i] != null && !exists(getActualInputType(new Socket(node.node, i)), ret[i])) {
                ret[i] = null;
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean exists(SpellType<T> type, Object o) {
        return type.exists((T) o);
    }

    public void apply(@NotNull ChannelSocket out, @Nullable Object returnValue) {
        markDirty();
        
        SpellType<?> returnType = out.node.type.getReturnTypes(this, out.node)[out.index];

        if (returnValue != null && !returnType.getValueClass().isInstance(returnValue)) {
            throw new IllegalArgumentException();
        }

        if (returnValue == null) output.remove(out);
        else output.put(out, returnValue);

        for (Socket in : nodes.get(new Socket(out.node, out.index))) {
            in.node.type.apply(this, new ChannelNode(in.node, out.channel));
        }
    }

    public void apply(@NotNull ChannelNode node, @NotNull Object[] returnValues) {
        SpellType<?>[] returnTypes = node.node.type.getReturnTypes(this, node.node);
        if (returnValues.length != returnTypes.length) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < returnValues.length; i++) {
            apply(new ChannelSocket(node.node, i, node.channel), returnValues[i]);
        }
    }

}
