package astavie.spellcrafting.api.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class Location {

	private final DimensionType dimension;
	private final Vec3d pos;

	public Location(World world, Vec3d pos) {
		this.dimension = world.getDimension().getType();
		this.pos = pos;
	}

	public Location(DimensionType dimension, Vec3d pos) {
		this.dimension = dimension;
		this.pos = pos;
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

}
