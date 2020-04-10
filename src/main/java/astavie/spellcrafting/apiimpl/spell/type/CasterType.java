package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.common.caster.CasterCapability;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CasterType implements IFocusType<ICaster> {

	@Override
	public INBT writeToNBT(ICaster object) {
		CompoundNBT compound = new CompoundNBT();

		if (object.isBlock()) {
			// Tile entity
			compound.putBoolean("block", true);

			TileEntity tile = object.getAsBlock();
			compound.putInt("dim", tile.getWorld().dimension.getType().getId());
			compound.putInt("x", tile.getPos().getX());
			compound.putInt("y", tile.getPos().getY());
			compound.putInt("z", tile.getPos().getZ());
		} else {
			// Entity
			compound.putBoolean("block", false);

			Entity entity = object.getAsEntity();
			compound.putInt("dim", entity.dimension.getId());
			compound.putInt("entity", entity.getEntityId());
		}

		return compound;
	}

	@Override
	public ICaster readFromNBT(INBT nbt) {
		CompoundNBT compound = (CompoundNBT) nbt;

		World world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(compound.getInt("dim")));
		ICapabilityProvider provider;

		if (compound.getBoolean("block")) {
			// Tile entity
			provider = world.getTileEntity(new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z")));
		} else {
			// Entity
			provider = world.getEntityByID(compound.getInt("entity"));
		}

		return provider.getCapability(CasterCapability.CASTER_CAPABILITY).orElseThrow(IllegalStateException::new);
	}

}
