package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.caster.ICaster;
import astavie.spellcrafting.common.caster.CasterCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class CasterType implements IFocusType<ICaster> {

	@Override
	public INBT writeToNBT(ICaster object) {
		CompoundNBT compound = new CompoundNBT();

		if (object.isBlock()) {
			// Tile entity
			compound.putString("type", "block");

			TileEntity tile = object.getAsBlock();
			compound.putInt("dim", tile.getWorld().dimension.getType().getId());
			compound.putInt("x", tile.getPos().getX());
			compound.putInt("y", tile.getPos().getY());
			compound.putInt("z", tile.getPos().getZ());
		} else if (object.getAsEntity() instanceof PlayerEntity) {
			// Player
			compound.putString("type", "player");
			compound.putUniqueId("player", object.getAsEntity().getUniqueID());
		} else {
			// Entity
			compound.putString("type", "entity");

			Entity entity = object.getAsEntity();
			compound.putInt("dim", entity.dimension.getId());
			compound.putInt("entity", entity.getEntityId());
		}

		return compound;
	}

	@Override
	public ICaster readFromNBT(INBT nbt) {
		CompoundNBT compound = (CompoundNBT) nbt;

		ICapabilityProvider provider = null;

		switch (compound.getString("type")) {
			case "block":
				World world1 = DimensionManager.getWorld(ServerLifecycleHooks.getCurrentServer(), DimensionType.getById(compound.getInt("dim")), false, false);
				if (world1 == null)
					return null;

				BlockPos pos = new BlockPos(compound.getInt("x"), compound.getInt("y"), compound.getInt("z"));
				if (!world1.isAreaLoaded(pos, 1))
					return null;

				provider = world1.getTileEntity(pos);
				break;
			case "player":
				provider = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUUID(compound.getUniqueId("player"));
				break;
			case "entity":
				World world2 = DimensionManager.getWorld(ServerLifecycleHooks.getCurrentServer(), DimensionType.getById(compound.getInt("dim")), false, false);
				if (world2 == null)
					return null;

				provider = world2.getEntityByID(compound.getInt("entity"));
				break;
		}

		if (provider == null)
			return null;

		return provider.getCapability(CasterCapability.CASTER_CAPABILITY).orElse(null);
	}

}
