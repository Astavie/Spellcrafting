package astavie.spellcrafting.spell.util;

import java.util.Optional;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class RaycastUtils {

    private RaycastUtils() {
    }

    public static HitResult raycast(World world, Vec3d min, Vec3d max, Entity ignore) {
        // Block
        ShapeType shapeType = RaycastContext.ShapeType.OUTLINE;
        FluidHandling fluid = RaycastContext.FluidHandling.NONE;
        ShapeContext entityPosition = ignore == null ? ShapeContext.absent() : ShapeContext.of(ignore);

        BlockHitResult result = BlockView.raycast(min, max, null, (context, pos) -> {
            BlockState blockState = world.getBlockState(pos);
            FluidState fluidState = world.getFluidState(pos);
            VoxelShape voxelShape = shapeType.get(blockState, world, pos, entityPosition);
            BlockHitResult blockHitResult = world.raycastBlock(min, max, pos, voxelShape, blockState);
            VoxelShape voxelShape2 = fluid.handled(fluidState) ? fluidState.getShape(world, pos) : VoxelShapes.empty();
            BlockHitResult blockHitResult2 = voxelShape2.raycast(min, max, pos);
            double d = blockHitResult == null ? Double.MAX_VALUE : min.squaredDistanceTo(blockHitResult.getPos());
            double e2 = blockHitResult2 == null ? Double.MAX_VALUE : min.squaredDistanceTo(blockHitResult2.getPos());
            return d <= e2 ? blockHitResult : blockHitResult2;
        }, context -> {
            Vec3d dir = min.subtract(max);
            return BlockHitResult.createMissed(max, Direction.getFacing(dir.x, dir.y, dir.z), new BlockPos(max));
        });

        Vec3d max2 = result.getPos();

        double e = min.squaredDistanceTo(max2);
        Entity entity2 = null;
        Vec3d vec3d = null;
        Box box = (ignore == null ? new Box(min, min) : ignore.getBoundingBox()).stretch(max2.subtract(min)).expand(1.0, 1.0, 1.0);
        for (Entity entity3 : world.getOtherEntities(ignore, box, entity -> !entity.isSpectator() && entity.collides())) {
            Vec3d vec3d2;
            double f;
            Box box2 = entity3.getBoundingBox().expand(entity3.getTargetingMargin());
            Optional<Vec3d> optional = box2.raycast(min, max2);
            if (box2.contains(min)) {
                if (!(e >= 0.0)) continue;
                entity2 = entity3;
                vec3d = optional.orElse(min);
                e = 0.0;
                continue;
            }
            if (!optional.isPresent() || !((f = min.squaredDistanceTo(vec3d2 = optional.get())) < e) && e != 0.0) continue;
            if (ignore != null && entity3.getRootVehicle() == ignore.getRootVehicle()) {
                if (e != 0.0) continue;
                entity2 = entity3;
                vec3d = vec3d2;
                continue;
            }
            entity2 = entity3;
            vec3d = vec3d2;
            e = f;
        }
        if (entity2 == null) {
            return result;
        }

        // Entity
        return new EntityHitResult(entity2, vec3d);
    }
    
}
