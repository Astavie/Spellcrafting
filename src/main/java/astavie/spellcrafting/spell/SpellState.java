package astavie.spellcrafting.spell;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import astavie.spellcrafting.api.spell.Spell;
import astavie.spellcrafting.api.util.ServerUtils;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;

public class SpellState extends PersistentState {

    private Map<UUID, Spell> spells = new HashMap<>();

    public static SpellState getInstance() {
        return ServerUtils.server.getOverworld().getPersistentStateManager().getOrCreate((tag) -> {
            SpellState s = new SpellState();
            s.readNbt(tag);
            return s;
        }, SpellState::new, "spellcrafting:spells");
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList spells = new NbtList();
        for (Spell spell : this.spells.values()) {
            spells.add(Spell.serialize(spell));
        }
        nbt.put("spells", spells);
        return nbt;
    }

    public void readNbt(NbtCompound nbt) {
        NbtList spells = nbt.getList("spells", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < spells.size(); i++) {
            Spell spell = Spell.deserialize(spells.getCompound(i));
            addSpell(spell);
        }
    }

    public Spell getSpell(UUID uuid) {
        return spells.get(uuid);
    }

    public void addSpell(Spell spell) {
        spells.put(spell.getUUID(), spell);
        markDirty();
    }

    public void removeSpell(UUID uuid) {
        spells.remove(uuid);
        markDirty();
    }

    public void onEvent(Spell.Event event, Object context) {
        // Prevent ConcurrentModificationException
        new HashSet<>(spells.values()).forEach(s -> s.onEvent(event, context));
    }
    
}
