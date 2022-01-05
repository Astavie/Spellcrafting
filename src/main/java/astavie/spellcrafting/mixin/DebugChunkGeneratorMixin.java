package astavie.spellcrafting.mixin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.DebugChunkGenerator;

@Mixin(DebugChunkGenerator.class)
public class DebugChunkGeneratorMixin {

	@Shadow @Final private static List<BlockState> BLOCK_STATES;

	@Mutable @Shadow @Final private static int X_SIDE_LENGTH;

	@Mutable @Shadow @Final private static int Z_SIDE_LENGTH;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void init(Registry<Biome> biomeRegistry, CallbackInfo info) {
		BLOCK_STATES.clear();
		BLOCK_STATES.addAll(StreamSupport.stream(Registry.BLOCK.spliterator(), false).flatMap((block) -> {
			return block.getStateManager().getStates().stream();
		}).collect(Collectors.toList()));
		X_SIDE_LENGTH = MathHelper.ceil(MathHelper.sqrt((float)BLOCK_STATES.size()));
		Z_SIDE_LENGTH = MathHelper.ceil((float)BLOCK_STATES.size() / (float)X_SIDE_LENGTH);
	}

}
