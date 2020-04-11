package astavie.spellcrafting.common.spell.bead;

import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.common.network.PacketHandler;
import astavie.spellcrafting.common.network.client.MessagePosition;
import astavie.spellcrafting.common.util.SpellUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.List;

public class BeadSwap implements IBead {

	@Override
	public int getFocusCount() {
		return 2;
	}

	@Override
	public List<IFocusType<?>> getApplicableTypes(int focus) {
		return ImmutableList.of(
				SpellcraftingAPI.instance().focusTypes().entity(),
				SpellcraftingAPI.instance().focusTypes().caster()
		);
	}

	@Override
	public boolean cast(ISpell spell, IBeadStack stack) {
		Entity e0 = SpellUtils.getEntity(spell, stack, 0);
		Entity e1 = SpellUtils.getEntity(spell, stack, 1);

		if (e0 == null || e1 == null)
			return false;

		// Get mounts
		Entity mount0 = e0.getRidingEntity();
		Entity mount1 = e1.getRidingEntity();

		List<Entity> passengers0 = e0.getPassengers();
		List<Entity> passengers1 = e1.getPassengers();

		// Swap location
		World world = e0.getEntityWorld();
		Vec3d pos = new Vec3d(e0.getPosX(), e0.getPosY(), e0.getPosZ());
		Vec2f rot = getRotation(e0);
		Vec3d motion = e0.getMotion();

		e0.detach();
		e1.detach();

		e0 = setPosition(e0, e1.getEntityWorld(), new Vec3d(e1.getPosX(), e1.getPosY(), e1.getPosZ()), getRotation(e1), e1.getMotion());
		e1 = setPosition(e1, world, pos, rot, motion);

		// Swap mounts
		if (e1 != null) {
			e1.stopRiding();
			if (mount0 != null)
				e1.startRiding(mount0);
			for (Entity e : passengers0)
				e.startRiding(e1);
		}

		if (e0 != null) {
			e0.stopRiding();
			if (mount1 != null)
				e0.startRiding(mount1);
			for (Entity e : passengers1)
				e.startRiding(e0);
		}

		return e0 != null && e1 != null;
	}

	private Vec2f getRotation(Entity e) {
		if (e instanceof LivingEntity) {
			return new Vec2f(e.rotationPitch, ((LivingEntity) e).rotationYawHead);
		} else {
			return new Vec2f(e.rotationPitch, e.rotationYaw);
		}
	}

	private Entity setPosition(final Entity e, World world, Vec3d pos, Vec2f rot, Vec3d motion) {
		// Copied from TeleportCommand
		if (e instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) e).teleport((ServerWorld) world, pos.x, pos.y, pos.z, rot.y, rot.x);
			e.setMotion(motion);

			PacketHandler.sendToPlayers(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), new MessagePosition(e, pos, rot));
			return e;
		} else {
			if (e.world == world) {
				e.setLocationAndAngles(pos.x, pos.y, pos.z, rot.y, rot.x);
				e.setRotationYawHead(rot.y);
				e.setMotion(motion);

				PacketHandler.sendToPlayers(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> e), new MessagePosition(e, pos, rot));
				return e;
			} else {
				Entity entity = e.getType().create(world);
				if (entity == null) {
					return null;
				}

				e.detach();
				e.dimension = world.dimension.getType();

				entity.copyDataFromOld(e);
				entity.setLocationAndAngles(pos.x, pos.y, pos.z, rot.y, rot.x);
				entity.setRotationYawHead(rot.y);
				entity.setMotion(motion);

				((ServerWorld) world).removeEntity(e);
				((ServerWorld) world).func_217460_e(entity);

				PacketHandler.sendToPlayers(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), new MessagePosition(entity, pos, rot));
				return entity;
			}
		}
	}

}
