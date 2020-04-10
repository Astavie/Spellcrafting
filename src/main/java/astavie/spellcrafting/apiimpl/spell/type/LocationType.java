package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.util.Location;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType;

public class LocationType implements IFocusType<Location> {

	@Override
	public INBT writeToNBT(Location object) {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("dim", object.getDimension().getId());
		compound.putDouble("vecx", object.getPos().x);
		compound.putDouble("vecy", object.getPos().y);
		compound.putDouble("vecz", object.getPos().z);
		compound.putInt("blockx", object.getBlock().getX());
		compound.putInt("blocky", object.getBlock().getY());
		compound.putInt("blockz", object.getBlock().getZ());
		return compound;
	}

	@Override
	public Location readFromNBT(INBT nbt) {
		CompoundNBT compound = (CompoundNBT) nbt;
		DimensionType type = DimensionType.getById(compound.getInt("dim"));
		Vec3d pos = new Vec3d(compound.getDouble("vecx"), compound.getDouble("vecy"), compound.getDouble("vecz"));
		BlockPos block = new BlockPos(compound.getInt("blockx"), compound.getInt("blocky"), compound.getInt("blockz"));
		return new Location(type, pos, block);
	}

}
