package astavie.spellcrafting.api.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class Location {

	private final DimensionType dimension;

	private final BlockPos block;
	private final Vec3d pos;

	public Location(World world, Vec3d pos) {
		this(world, pos, new BlockPos(pos));
	}

	public Location(DimensionType dimension, Vec3d pos) {
		this(dimension, pos, new BlockPos(pos));
	}

	public Location(World world, Vec3d pos, BlockPos block) {
		this(world.getDimension().getType(), pos, block);
	}

	public Location(DimensionType dimension, Vec3d pos, BlockPos block) {
		this.dimension = dimension;
		this.pos = pos;
		this.block = block;
	}

	/**
	 * Note that this method will load the world if it is unloaded. Please use {@link #getDimension()} if that isn't your intention.
	 * @return the world of this location
	 */
	public World getWorld() {
		return ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
	}

	/**
	 * @return the dimension of this location
	 */
	public DimensionType getDimension() {
		return dimension;
	}

	/**
	 * @return the coordinates of this location
	 */
	public Vec3d getPos() {
		return pos;
	}

	public BlockPos getBlock() {
		return block;
	}

}
