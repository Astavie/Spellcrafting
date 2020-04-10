package astavie.spellcrafting.common.spell.bead;

import astavie.spellcrafting.api.spell.IBead;
import astavie.spellcrafting.api.spell.IBeadStack;
import astavie.spellcrafting.api.spell.IFocusType;
import astavie.spellcrafting.api.spell.ISpell;
import astavie.spellcrafting.apiimpl.SpellcraftingAPI;
import astavie.spellcrafting.common.util.SpellUtils;
import com.google.common.collect.ImmutableList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
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

		// Swap location
		Vec3d pos = new Vec3d(e0.getPosX(), e0.getPosY(), e0.getPosZ());
		Vec2f rot = getRotation(e0);

		setPosition(e0, new Vec3d(e1.getPosX(), e1.getPosY(), e1.getPosZ()), getRotation(e1));
		setPosition(e1, pos, rot);

		// Swap mounts
		Entity mount0 = e0.getRidingEntity();
		Entity mount1 = e1.getRidingEntity();

		e0.stopRiding();
		e1.stopRiding();

		if (mount0 != null)
			e1.startRiding(mount0);
		if (mount1 != null)
			e0.startRiding(mount1);

		return true;
	}

	private Vec2f getRotation(Entity e) {
		if (e instanceof LivingEntity) {
			return new Vec2f(e.rotationPitch, ((LivingEntity) e).rotationYawHead);
		} else {
			return new Vec2f(e.rotationPitch, e.rotationYaw);
		}
	}

	private void setPosition(Entity e, Vec3d pos, Vec2f rot) {
		if (e instanceof ServerPlayerEntity) {
			((ServerPlayerEntity) e).connection.setPlayerLocation(pos.x, pos.y, pos.z, rot.y, rot.x, EnumSet.noneOf(SPlayerPositionLookPacket.Flags.class));
		} else {
			e.setLocationAndAngles(pos.x, pos.y, pos.z, rot.y, rot.x);
			e.setRotationYawHead(rot.y);
		}
	}

}
