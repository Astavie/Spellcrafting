package astavie.spellcrafting.client.render.block.entity;

import astavie.spellcrafting.block.MagicCircleBlock;
import astavie.spellcrafting.block.entity.MagicCircleBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

public class MagicCircleBlockEntityRenderer implements BlockEntityRenderer<MagicCircleBlockEntity> {

    public MagicCircleBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public void render(MagicCircleBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MagicCircleBlock block = (MagicCircleBlock) entity.getCachedState().getBlock();
        Direction right = entity.getCachedState().get(MagicCircleBlock.FACING);
        Direction down = right.rotateYClockwise();

        for (int y = 0; y < block.size; y++) {
            for (int x = 0; x < block.size; x++) {
                ItemStack stack = entity.getItem(x + y * block.size);
                if (stack.isEmpty()) continue;

                matrices.push();

                double offset = Math.sin((entity.getWorld().getTime() + tickDelta) / 8.0) / 8.0;

                BlockPos pos = BlockPos.ORIGIN.offset(right, x).offset(down, y);
                matrices.translate(pos.getX() + 0.5, 0.25 + offset, pos.getZ() + 0.5);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 4));

                int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().add(pos));
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, entity.getPos().add(pos).hashCode());
        
                matrices.pop();
            }
        }
    }
}
