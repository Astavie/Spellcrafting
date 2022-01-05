package astavie.spellcrafting.client.render.block.entity;

import astavie.spellcrafting.block.entity.MagicCircleBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class MagicCircleBlockEntityRenderer implements BlockEntityRenderer<MagicCircleBlockEntity> {

    public MagicCircleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(MagicCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) return;

        matrices.push();

        double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;

        matrices.translate(0.5, 0.25 + offset, 0.5);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 4));

        int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().up());
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, entity.getPos().hashCode());
 
        matrices.pop();
    }
}
