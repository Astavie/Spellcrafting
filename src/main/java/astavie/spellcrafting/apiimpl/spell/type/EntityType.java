package astavie.spellcrafting.apiimpl.spell.type;

import astavie.spellcrafting.api.spell.IFocusType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class EntityType implements IFocusType<Entity> {

	@Override
	public INBT writeToNBT(Entity object) {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("dim", object.dimension.getId());
		compound.putInt("entity", object.getEntityId());
		return compound;
	}

	@Override
	public Entity readFromNBT(INBT nbt) {
		CompoundNBT compound = (CompoundNBT) nbt;
		World world = ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(compound.getInt("dim")));
		return world.getEntityByID(compound.getInt("entity"));
	}

}
