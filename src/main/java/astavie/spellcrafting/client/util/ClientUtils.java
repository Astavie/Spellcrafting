package astavie.spellcrafting.client.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class ClientUtils {

	private ClientUtils() {
	}

	public static RayTraceResult rayTrace(Entity entity, float partialTicks, double reach) {
		RayTraceResult objectMouseOver = entity.pick(reach, partialTicks, false);
		Vec3d vec3d = entity.getEyePosition(partialTicks);
		double d1 = objectMouseOver.getHitVec().squareDistanceTo(vec3d);

		Vec3d vec3d1 = entity.getLook(partialTicks);
		Vec3d vec3d2 = vec3d.add(vec3d1.x * reach, vec3d1.y * reach, vec3d1.z * reach);
		AxisAlignedBB axisalignedbb = entity.getBoundingBox().expand(vec3d1.scale(reach)).grow(1, 1, 1);
		EntityRayTraceResult entityraytraceresult = ProjectileHelper.rayTraceEntities(entity, vec3d, vec3d2, axisalignedbb, (p_215312_0_) -> !p_215312_0_.isSpectator() && p_215312_0_.canBeCollidedWith(), d1);
		if (entityraytraceresult != null) {
			Vec3d vec3d3 = entityraytraceresult.getHitVec();
			double d2 = vec3d.squareDistanceTo(vec3d3);
			if (d2 < d1) {
				objectMouseOver = entityraytraceresult;
			}
		}

		return objectMouseOver;
	}

}
