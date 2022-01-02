package astavie.spellcrafting.api.spell;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public interface Attunable {

    BlockApiLookup<Attunable, Direction> BLOCK_ATTUNABLE = BlockApiLookup.get(new Identifier("spellcrafting:attunable"), Attunable.class, Direction.class);
    EntityApiLookup<Attunable, Void> ENTITY_ATTUNABLE = EntityApiLookup.get(new Identifier("spellcrafting:attunable"), Attunable.class, Void.class);

    default boolean isAttunedTo(@NotNull Attunable attunable) {
        return attunable.getAttunement().equals(getAttunement());
    }

    void attuneTo(@Nullable Attunable attunable);

    @NotNull UUID getAttunement();

    @NotNull
    Vec3d getCenter();
    
}
