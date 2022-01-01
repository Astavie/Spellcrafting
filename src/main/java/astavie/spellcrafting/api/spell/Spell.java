package astavie.spellcrafting.api.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import astavie.spellcrafting.api.spell.node.SpellNode;
import astavie.spellcrafting.api.spell.target.DistancedTarget;
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtNull;
import net.minecraft.util.Identifier;

public class Spell {

    public static record Socket(@NotNull SpellNode node, int index) {
    }

    public static record Event(@NotNull Identifier eventType, @NotNull NbtElement argument) {

        public static final @NotNull Identifier TARGET_ID = new Identifier("spellcrafting:target");
        public static final @NotNull Identifier HIT_ID = new Identifier("spellcrafting:hit");
        public static final @NotNull Identifier TICK_ID = new Identifier("spellcrafting:tick");

        public static final @NotNull Event TARGET = new Event(TARGET_ID, NbtNull.INSTANCE);
        
    }

    private final Map<Socket, Object> output = new HashMap<>();
    private final Multimap<Socket, Socket> nodes;
    private final Map<Socket, Socket> inverse = new HashMap<>();
    private final SpellNode start;

    private final ItemList components = new ItemList();
    private Caster caster;
    private Target target;

    private final Multimap<Event, SpellNode> events = HashMultimap.create();
    private final Map<Event, Object> eventsThisTick = new HashMap<>();

    public Spell(SpellNode start, Multimap<Socket, Socket> nodes) {
        if (start.getParameters().length > 0) throw new IllegalArgumentException("Illegal start node");
        this.start = start;
        this.nodes = nodes;
        
        for (Map.Entry<Socket, Socket> entry : nodes.entries()) {
            if (inverse.containsKey(entry.getValue())) throw new IllegalArgumentException("Spell has edges that combine");
            inverse.put(entry.getValue(), entry.getKey());
        }

        // Check graph and calculate multipliers
        Map<SpellNode, Integer> factors = new HashMap<>();
        factors.put(start, 1);
        continueFactor(factors, new HashSet<>(), start);

        // Add components
        for (Entry<SpellNode, Integer> entry : factors.entrySet()) {
            components.addItemList(entry.getKey().getComponents(), entry.getValue());
        }
    }

    private void continueFactor(Map<SpellNode, Integer> factors, Set<SpellNode> blacklist, SpellNode node) {
        int multiplier = factors.get(node);

        Set<SpellNode> nextBlacklist = new HashSet<>(blacklist);
        nextBlacklist.add(node);

        int returnTypes = node.getReturnTypes().length;
        for (int i = 0; i < returnTypes; i++) {
            for (Socket in : nodes.get(new Socket(node, i))) {
                if (nextBlacklist.contains(in.node)) throw new IllegalArgumentException("Cyclic spell");
                if (node.getReturnTypes()[i] != in.node.getParameters()[in.index]) throw new IllegalArgumentException("Spell has illegal connections");

                int m = factors.getOrDefault(in.node, 0);
                if (m < multiplier * node.getComponentFactor(i)) {
                    factors.put(in.node, multiplier * node.getComponentFactor(i));
                    continueFactor(factors, nextBlacklist, in.node);
                }
            }
        }
    }

    public @Nullable Object getOutput(@NotNull Socket socket) {
        return output.get(socket);
    }

    public boolean isActive() {
        return caster != null;
    }

    public long getTime() {
        return caster.asTarget().getWorld().getServer().getOverworld().getTime();
    }

    public boolean inRange(@NotNull DistancedTarget target) {
        if (target.origin() == null || target.origin().asAttunable() == null) return false;

        double range = caster.getRange();
        return target.origin().asAttunable().isAttunedTo(caster) && target.origin().getPos().squaredDistanceTo(target.target().getPos()) <= range * range;
    }

    public @Nullable Caster getCaster() {
        return caster;
    }

    public @Nullable Target getTarget() {
        return target;
    }

    public @NotNull ItemList components() {
        return components;
    }

    public void end() {
        output.clear();
        events.clear();
        eventsThisTick.clear();
        caster = null;
    }

    public boolean onTarget(@NotNull Caster caster, @NotNull Target target) {
        if (!isActive()) {
            // Use components
			Transaction transaction = Transaction.openOuter();
            ItemList missing = caster.useComponents(components, transaction);
            if (!missing.isEmpty()) {
                transaction.abort();
                return false;
            }

            // Cast spell!
            transaction.commit();
            this.caster = caster;
            this.target = target;
            start.apply(this, false);
            this.target = null;
            return true;
        } else if (events.containsKey(Event.TARGET)) {
            this.caster = caster;
            onEvent(Event.TARGET, target);
            return true;
        }

        return false;
    }

    public void registerEvent(@NotNull Event event, @NotNull SpellNode handler) {
        if (eventsThisTick.containsKey(event)) {
            handler.onEvent(this, event, eventsThisTick.get(event));
        } else {
            events.put(event, handler);
        }
    }

    public void cancelEvents(@NotNull SpellNode handler) {
        events.values().remove(handler);
    }

    public void onEvent(@NotNull Event event, Object context) {
        for (SpellNode node : events.removeAll(event)) {
            node.onEvent(this, event, context);
        }
        if (event.eventType == Event.TICK_ID) {
            eventsThisTick.clear();
        } else {
            eventsThisTick.put(event, context);
        }
    }

    public void schedule(@NotNull SpellNode handler) {
        registerEvent(new Event(Event.TICK_ID, NbtLong.of(getTime() + 1)), handler);
    }

    public @NotNull Object[] getInput(@NotNull SpellNode node) {
        int parameters = node.getParameters().length;
        Object[] ret = new Object[parameters];
        for (int i = 0; i < parameters; i++) {
            ret[i] = output.get(inverse.get(new Socket(node, i)));
        }
        return ret;
    }

    public void apply(@NotNull SpellNode node, int index, @Nullable Object returnValue) {
        SpellType returnType = node.getReturnTypes()[index];

        if (returnValue != null && !returnType.valueType().isInstance(returnValue)) {
            throw new IllegalArgumentException();
        }

        Socket out = new Socket(node, index);
        output.put(out, returnValue);

        for (Socket in : nodes.get(out)) {
            in.node.apply(this, returnType == SpellType.TIME);
        }
    }

    public void apply(@NotNull SpellNode node, @NotNull Object[] returnValues) {
        SpellType[] returnTypes = node.getReturnTypes();
        if (returnValues.length != returnTypes.length) {
            throw new IllegalArgumentException();
        }

        // Iterate in reverse order so the TIME signal, if it exists, is last
        // TODO: Come up with a better solution
        for (int i = returnValues.length - 1; i >= 0; i--) {
            apply(node, i, returnValues[i]);
        }
    }

}
