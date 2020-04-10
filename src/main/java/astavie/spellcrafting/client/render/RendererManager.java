package astavie.spellcrafting.client.render;

import astavie.spellcrafting.Spellcrafting;
import astavie.spellcrafting.api.item.ISpellItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Spellcrafting.MODID, value = {Dist.CLIENT})
public class RendererManager {

	public static CastingPlayerRenderer castingRendererSmallArms, castingRendererBigArms;

	public static void register() {
		EntityRendererManager manager = Minecraft.getInstance().getRenderManager();
		castingRendererBigArms = new CastingPlayerRenderer(manager, false);
		castingRendererSmallArms = new CastingPlayerRenderer(manager, true);
	}

	@SubscribeEvent
	public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
		if (!(event.getRenderer() instanceof CastingPlayerRenderer)) {
			PlayerEntity player = event.getPlayer();
			ItemStack held = player.getActiveItemStack();

			if (held.isEmpty() || !(held.getItem() instanceof ISpellItem))
				return;

			if (((ISpellItem) held.getItem()).useCastingAnimation(player.world, player, player.getActiveHand(), held, player.getItemInUseCount())) {
				event.setCanceled(true);

				float yaw = MathHelper.lerp(event.getPartialRenderTick(), event.getEntity().prevRotationYaw, event.getEntity().rotationYaw);

				PlayerRenderer renderer = event.getRenderer().entityModel.smallArms ? castingRendererSmallArms : castingRendererBigArms;
				renderer.render((AbstractClientPlayerEntity) player, yaw, event.getPartialRenderTick(), event.getMatrixStack(), event.getBuffers(), event.getLight());
			}
		}
	}

}
