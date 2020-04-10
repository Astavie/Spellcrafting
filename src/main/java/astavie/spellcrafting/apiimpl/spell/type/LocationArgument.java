package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IArgumentType;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class LocationArgument implements IArgumentType<Location> {

	@Override
	public INBT writeToNBT(Location object) {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("dim", object.getDimension().getId());
		compound.putDouble("x", object.getPos().x);
		compound.putDouble("y", object.getPos().y);
		compound.putDouble("z", object.getPos().z);
		return compound;
	}

	@Override
	public Location readFromNBT(INBT nbt) {
		CompoundNBT compound = (CompoundNBT) nbt;
		DimensionType type = DimensionType.getById(compound.getInt("dim"));
		Vec3d pos = new Vec3d(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
		return new Location(type, pos);
	}

}
