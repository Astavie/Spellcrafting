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

        BlockPos middle = BlockPos.ORIGIN.offset(right, block.size - 1).offset(down, block.size - 1);

        for (int y = 0; y < block.size; y++) {
            for (int x = 0; x < block.size; x++) {
                ItemStack stack = entity.getItem(x + y * block.size);
                if (stack.isEmpty()) continue;

                matrices.push();

                double offset = Math.sin((entity.getWorld().getTime() + tickDelta + (x + y * block.size) * 10) / 8.0) / 16.0;

                BlockPos pos = BlockPos.ORIGIN.offset(right, x).offset(down, y);

                matrices.translate(middle.getX() / 2.0 + 0.5, 0.25 + offset, middle.getZ() / 2.0 + 0.5);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime()) * 4));
                matrices.translate(pos.getX() / 2.0 - middle.getX() / 4.0, 0, pos.getZ() / 2.0 - middle.getZ() / 4.0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 4));

                int lightAbove = WorldRenderer.getLightmapCoordinates(entity.getWorld(), entity.getPos().add(pos));
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, lightAbove, overlay, matrices, vertexConsumers, entity.getPos().add(pos).hashCode());
        
                matrices.pop();
            }
        }
    }
}
