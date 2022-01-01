package astavie.spellcrafting.api.spell;

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

    boolean isAttunedTo(@NotNull Caster caster);

    void attuneTo(@Nullable Caster caster);

    @NotNull
    Vec3d getCenter();
    
}