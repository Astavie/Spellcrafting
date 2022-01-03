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
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import astavie.spellcrafting.spell.SpellState;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtNull;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

// TODO: Turn this into an interface with an Impl
public class Spell {

    public static class Node {
        private final NodeType type;
        public Node(@NotNull NodeType type) {
            this.type = type;
        }
    }

    public static record Socket(@NotNull Node node, int index) {
    }

    public static record Event(@NotNull Identifier eventType, @NotNull NbtElement argument) {

        public static final @NotNull Identifier TARGET_ID = new Identifier("spellcrafting:target");
        public static final @NotNull Identifier HIT_ID = new Identifier("spellcrafting:hit");
        public static final @NotNull Identifier LAND_ID = new Identifier("spellcrafting:land");
        public static final @NotNull Identifier TICK_ID = new Identifier("spellcrafting:tick");

        public static final @NotNull Event TARGET = new Event(TARGET_ID, NbtNull.INSTANCE); // TODO: Argument will be DyeColor
        
    }

    private ServerWorld world;
    private Target caster;
    private final Map<Event, Object> eventsThisTick = new HashMap<>();

    private final UUID uuid;
    private final Set<Node> start;
    private final Multimap<Socket, Socket> nodes;
    private final Map<Socket, Socket> inverse = new HashMap<>();
    private final Map<Socket, Object> output = new HashMap<>();
    private final Multimap<Event, Node> events = HashMultimap.create();
    private final ItemList components = new ItemList();

    private Spell(ServerWorld world, UUID uuid) {
        this.start = new HashSet<>();
        this.nodes = HashMultimap.create();
        this.uuid = uuid;
        this.world = world;
    }

    public Spell(Set<Node> start, Multimap<Socket, Socket> nodes) {
        if (start.stream().anyMatch(n -> n.type.getParameters().length > 0)) throw new IllegalArgumentException("Illegal start node");
        this.start = start;
        this.nodes = nodes;
        this.uuid = UUID.randomUUID();
        
        for (Map.Entry<Socket, Socket> entry : nodes.entries()) {
            if (inverse.containsKey(entry.getValue())) throw new IllegalArgumentException("Spell has edges that combine");
            inverse.put(entry.getValue(), entry.getKey());
        }

        calculateComponents();
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
            components.addItemList(node.type.getComponents(this, node));
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

        Map<Node, Event> inverseEvents = new HashMap<>();
        for (Map.Entry<Event, Node> entry : spell.events.entries()) {
            inverseEvents.put(entry.getValue(), entry.getKey());
        }

        NbtList nodeList = new NbtList();
        NbtList socketList = new NbtList();

        // Pass 0: starting nodes
        for (Node node : spell.start) {
            nodes.put(node, nodeList.size());

            NbtCompound cmp = new NbtCompound();
            cmp.putString("type", NodeType.REGISTRY.getId(node.type).toString());
            nodeList.add(cmp);
        }

        // Pass 0: add all nodes with events
        for (Socket in : spell.inverse.keySet()) {
            if (!nodes.containsKey(in.node)) {
                nodes.put(in.node, nodeList.size());

                NbtCompound cmp = new NbtCompound();
                cmp.putString("type", NodeType.REGISTRY.getId(in.node.type).toString());
                if (inverseEvents.containsKey(in.node)) {
                    cmp.putString("event", inverseEvents.get(in.node).eventType.toString());
                    cmp.put("arg", inverseEvents.get(in.node).argument);
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
            
            if (spell.output.containsKey(out)) {
                SpellType<?> type = out.node.type.getReturnTypes(spell, out.node)[out.index];
                cmp.put("value", SpellType.serialize(type, spell.output.get(out)));
            }

            socketList.add(cmp);
        }

        // Pass 2: add all node connections
        for (Node node : nodes.keySet()) {
            int parameters = node.type.getParameters().length;
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
        if (spell.caster != null) cmp.put("caster", Target.serialize(spell.caster));
        return cmp;
    }

    public static Spell deserialize(@NotNull NbtCompound nbt, ServerWorld world) {
        // TODO: remove dependency on world

        NbtList nodes = nbt.getList("nodes", NbtElement.COMPOUND_TYPE);
        NbtList sockets = nbt.getList("sockets", NbtElement.COMPOUND_TYPE);

        Node[] totalNodes = new Node[nodes.size()];
        Socket[] totalSockets = new Socket[sockets.size()];

        Spell spell = new Spell(world, nbt.getUuid("UUID"));
        if (nbt.contains("caster")) spell.caster = Target.deserialize(nbt.getCompound("caster"), world);

        // Phase 0: get all nodes with events
        for (int i = 0; i < nodes.size(); i++) {
            NbtCompound cmp = nodes.getCompound(i);
            Node node = new Node(NodeType.REGISTRY.get(new Identifier(cmp.getString("type"))));
            totalNodes[i] = node;

            if (node.type.getParameters().length == 0) {
                spell.start.add(node);
            }

            if (cmp.contains("event")) {
                Event event = new Event(new Identifier(cmp.getString("event")), cmp.get("arg"));
                spell.events.put(event, node);
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
            int parameters = node.type.getParameters().length;

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
            if (cmp.contains("value")) {
                Socket socket = totalSockets[i];
                SpellType<?> type = socket.node.type.getReturnTypes(spell, socket.node)[socket.index];
                spell.output.put(socket, type.deserialize(cmp.get("value"), world));
            }
        }

        spell.calculateComponents();
        return spell;
    }

    private void continueFactor(Set<Node> total, Set<Node> blacklist, Node node) {
        Set<Node> nextBlacklist = new HashSet<>(blacklist);
        nextBlacklist.add(node);

        int returnTypes = node.type.getReturnTypes(this, node).length;
        for (int i = 0; i < returnTypes; i++) {
            for (Socket in : nodes.get(new Socket(node, i))) {
                if (nextBlacklist.contains(in.node)) throw new IllegalArgumentException("Cyclic spell");
                if (!in.node.type.getParameters()[in.index].getValueClass().isAssignableFrom(node.type.getReturnTypes(this, node)[i].getValueClass())) throw new IllegalArgumentException("Spell has illegal connections");

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

    public @Nullable Object getOutput(@NotNull Socket socket) {
        return output.get(socket);
    }

    public long getTime() {
        // TODO: Back to global time
        return caster.getWorld().getTime();
    }

    public boolean inRange(@NotNull DistancedTarget target) {
        // Is attuned
        if (
            target.getOrigin() == null ||
            target.getOrigin().asAttunable() == null ||
           !target.getOrigin().asAttunable().isAttunedTo(caster.asAttunable())
        ) return false;

        // Check range
        double range = caster.asCaster().getRange();
        return target.getOrigin().getPos().squaredDistanceTo(target.getTarget().getPos()) <= range * range + 0.1;
    }

    public @Nullable Target getCaster() {
        return caster;
    }

    public @NotNull ItemList components() {
        return components;
    }

    public void end() {
        // TODO: API breach
        SpellState.of(world).removeSpell(uuid);
        output.clear();
        events.clear();
        eventsThisTick.clear();
        caster = null;
    }

    public boolean onTarget(@NotNull Caster caster, @NotNull Target target) {
        if (events.isEmpty()) {
            // Use components
			Transaction transaction = Transaction.openOuter();
            ItemList missing = caster.useComponents(components, transaction);
            if (!missing.isEmpty()) {
                transaction.abort();
                return false;
            }

            // Cast spell!
            transaction.commit();
            this.caster = caster.asTarget();
    
            // TODO: API breach
            SpellState.of((ServerWorld) caster.asTarget().getWorld()).addSpell(this);

            for (Node node : start) node.type.apply(this, node);
            onEvent(Event.TARGET, target);

            return true;
        } else if (events.containsKey(Event.TARGET)) {
            this.caster = caster.asTarget();
            onEvent(Event.TARGET, target);
            return true;
        }

        return false;
    }

    public void registerEvent(@NotNull Event event, @NotNull Node handler) {
        cancelEvents(handler);
        if (eventsThisTick.containsKey(event)) {
            handler.type.onEvent(this, handler, event, eventsThisTick.get(event));
        } else {
            events.put(event, handler);
        }
    }

    public boolean hasEvent(@NotNull Node handler) {
        return events.values().contains(handler);
    }

    public void cancelEvents(@NotNull Node handler) {
        events.values().remove(handler);
    }

    public void onEvent(@NotNull Event event, Object context) {
        if (event.eventType == Event.TICK_ID && events.isEmpty()) {
            end();
            return;
        }
        
        for (Node node : events.removeAll(event)) {
            node.type.onEvent(this, node, event, context);
        }
        if (event.eventType == Event.TICK_ID) {
            eventsThisTick.clear();
        } else {
            eventsThisTick.put(event, context);
        }
    }

    public void schedule(@NotNull Node handler) {
        registerEvent(new Event(Event.TICK_ID, NbtLong.of(getTime() + 1)), handler);
    }

    public @NotNull Object[] getInput(@NotNull Node node) {
        SpellType<?>[] parameters = node.type.getParameters();
        Object[] ret = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            ret[i] = output.get(inverse.get(new Socket(node, i)));
            if (ret[i] != null && !exists(getActualInputType(new Socket(node, i)), ret[i])) {
                ret[i] = null;
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean exists(SpellType<T> type, Object o) {
        return type.exists((T) o);
    }

    public void apply(@NotNull Socket out, @Nullable Object returnValue) {
        SpellType<?> returnType = out.node.type.getReturnTypes(this, out.node)[out.index];

        if (returnValue != null && !returnType.getValueClass().isInstance(returnValue)) {
            throw new IllegalArgumentException();
        }

        if (returnValue == null) output.remove(out);
        else output.put(out, returnValue);

        for (Socket in : nodes.get(out)) {
            in.node.type.apply(this, in.node);
        }
    }

    public void apply(@NotNull Node node, @NotNull Object[] returnValues) {
        SpellType<?>[] returnTypes = node.type.getReturnTypes(this, node);
        if (returnValues.length != returnTypes.length) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < returnValues.length; i++) {
            apply(new Socket(node, i), returnValues[i]);
        }
    }

}
