package astavie.spellcrafting.api.spell;

import java.util.Arrays;
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
import astavie.spellcrafting.api.spell.target.Target;
import astavie.spellcrafting.api.util.ItemList;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;

public class Spell {

    public static record Connection(@NotNull SpellNode node, int index) {
    }

    public static record Event<T>(@NotNull Identifier eventType, @Nullable NbtElement argument) {

        public static final @NotNull Identifier TARGET_ID = new Identifier("spellcrafting:target");
        public static final @NotNull Identifier HIT_ID = new Identifier("spellcrafting:hit");
        public static final @NotNull Identifier TICK_ID = new Identifier("spellcrafting:tick");

        public static final @NotNull Event<Target> TARGET = new Event<>(TARGET_ID, null);
        
    }

    private final Map<SpellNode, Object[]> input = new HashMap<>();
    private final Map<SpellNode, Connection[]> nodes;
    private final SpellNode start;

    private final ItemList components = new ItemList();
    private Caster caster;
    private Target target;

    private final Multimap<Event<?>, SpellNode> events = HashMultimap.create();

    public Spell(SpellNode start, Map<SpellNode, Connection[]> nodes) {
        if (start.parameters().length > 0) throw new IllegalArgumentException("Illegal start node");
        this.start = start;
        this.nodes = nodes;

        for (SpellNode node : nodes.keySet()) {
            input.put(node, new Object[node.parameters().length]);
        }

        // Check graph and calculate multipliers
        Map<SpellNode, Integer> factors = new HashMap<>();
        factors.put(start, 1);
        continueFactor(factors, new HashSet<>(), start);

        if (factors.size() != nodes.size()) throw new IllegalArgumentException("Spell has multiple components");

        // Add components
        for (Entry<SpellNode, Integer> entry : factors.entrySet()) {
            components.addItemList(entry.getKey().components(), entry.getValue());
        }
    }

    private void continueFactor(Map<SpellNode, Integer> factors, Set<SpellNode> blacklist, SpellNode node) {
        int multiplier = factors.get(node);
        Connection[] conn = nodes.get(node);

        Set<SpellNode> nextBlacklist = new HashSet<>(blacklist);
        nextBlacklist.add(node);

        for (int i = 0; i < conn.length; i++) {
            if (conn[i] == null) continue;

            if (nextBlacklist.contains(conn[i].node)) throw new IllegalArgumentException("Cyclic spell");
            if (node.returnTypes()[i] != conn[i].node.parameters()[conn[i].index]) throw new IllegalArgumentException("Spell has illegal connections");

            int m = factors.getOrDefault(conn[i].node, 0);
            if (m < multiplier * node.componentFactor(i)) {
                factors.put(conn[i].node, multiplier * node.componentFactor(i));
                continueFactor(factors, nextBlacklist, conn[i].node);
            }
        }
    }

    public long getTime() {
        return caster.asTarget().getWorld().getServer().getOverworld().getTime();
    }

    public boolean inRange(@NotNull Target target) {
        return target.inRange(caster.getRange());
    }

    public @Nullable Caster caster() {
        return caster;
    }

    public @Nullable Target target() {
        return target;
    }

    public @NotNull ItemList components() {
        return components;
    }

    public void end() {
        for (SpellNode node : nodes.keySet()) {
            Arrays.fill(input.get(node), null);
        }
        events.clear();
        caster = null;
    }

    public boolean onTarget(@NotNull Caster caster, @NotNull Target target) {
        if (this.caster == null) {
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
            apply(start, start.apply(this, new Object[0]));
            this.target = null;
            return true;
        } else if (events.containsKey(Event.TARGET)) {
            this.caster = caster;
            onEvent(Event.TARGET, target);
            return true;
        }

        return false;
    }

    public void registerEvent(@NotNull Event<?> event, @NotNull SpellNode handler) {
        events.put(event, handler);
    }

    public <T> void onEvent(@NotNull Event<T> event, T context) {
        for (SpellNode node : events.removeAll(event)) {
            node.onEvent(this, input.get(node), event, context);
        }
    }

    public void apply(@NotNull SpellNode node, @NotNull Object[] returnValues) {
        SpellType[] returnTypes = node.returnTypes();
        if (returnValues.length != returnTypes.length) {
            throw new IllegalArgumentException();
        }

        Connection[] conn = this.nodes.getOrDefault(node, new Connection[returnTypes.length]);

        // Iterate in reverse order so the TIME signal, if it exists, is last
        // TODO: Come up with a better solution
        for (int i = conn.length - 1; i >= 0; i--) {
            if (returnValues[i] != null && !returnTypes[i].valueType().isInstance(returnValues[i])) {
                throw new IllegalArgumentException();
            }

            if (conn[i] == null) continue;

            // Apply if this has no effect or we are sending a TIME signal
            boolean apply = conn[i].node.applyOnChange() || returnTypes[i] == SpellType.TIME;

            Object[] input = this.input.get(conn[i].node);
            input[conn[i].index] = returnValues[i];

            if (apply) {
                Object[] returns = conn[i].node.apply(this, input);
                apply(conn[i].node, returns);
            }
        }
    }

}
