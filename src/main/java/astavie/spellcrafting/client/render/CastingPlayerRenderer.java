package astavie.spellcrafting.client.render;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.util.HandSide;

public class CastingPlayerRenderer extends PlayerRenderer {

	public CastingPlayerRenderer(EntityRendererManager manager, boolean smallArms) {
		super(manager, smallArms);
	}

	@Override
	protected boolean isVisible(AbstractClientPlayerEntity entity) {
		// This code is here because this function is called right after the arm rotations are set
		if (entity.getPrimaryHand() == HandSide.LEFT) {
			// Set arm rotation
			entityModel.bipedLeftArm.rotateAngleX = -1.57079632679F + entityModel.bipedHead.rotateAngleX;
			entityModel.bipedLeftArm.rotateAngleY = entityModel.bipedHead.rotateAngleY;

			// Copy to sleeves
			entityModel.bipedLeftArmwear.copyModelAngles(entityModel.bipedLeftArm);
		} else {
			// Set arm rotation
			entityModel.bipedRightArm.rotateAngleX = -1.57079632679F + entityModel.bipedHead.rotateAngleX;
			entityModel.bipedRightArm.rotateAngleY = entityModel.bipedHead.rotateAngleY;

			// Copy to sleeves
			entityModel.bipedRightArmwear.copyModelAngles(entityModel.bipedRightArm);
		}

		// Copied from LivingRenderer
		return !entity.isInvisible();
	}

}
