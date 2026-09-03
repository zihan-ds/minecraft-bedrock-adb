package dev.bedrockadb.mixin;

import dev.bedrockadb.rule.BorderGate;
import dev.bedrockadb.state.BorderColumnIndex;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Core of the vertical column barrier: {@link Entity#findCollisionsForMovement}
 * collects the voxel shapes that {@code Entity.move} slides against on every
 * tick (players, mobs, items, boats, minecarts, ...). Any shape appended here
 * therefore acts like a real wall for movement-based entities.
 *
 * <p>For every border column whose cell the moving entity's box overlaps we
 * append a full-height obstacle (world bottom to top) - unless the entity is
 * allowed to pass by {@link BorderGate}. That makes the border column
 * impassable at every Y: it can no longer be crossed by jumping over it,
 * flying above it or tunnelling underneath it.
 */
@Mixin(Entity.class)
public abstract class EntityBorderCollisionMixin {
   private static final int MAX_COLUMN_SPAN = 64;

   @Inject(method = "findCollisionsForMovement", at = @At("RETURN"), cancellable = true)
   private static void bedrockAdb$appendColumnBarriers(
      Entity entity, World world, List<VoxelShape> collisions, Box box,
      CallbackInfoReturnable<List<VoxelShape>> cir
   ) {
      if (entity == null || world == null || cir.getReturnValue() == null || box == null) {
         return;
      }
      int minX = (int) Math.floor(box.minX);
      int maxX = (int) Math.floor(box.maxX);
      int minZ = (int) Math.floor(box.minZ);
      int maxZ = (int) Math.floor(box.maxZ);
      if (maxX - minX > MAX_COLUMN_SPAN || maxZ - minZ > MAX_COLUMN_SPAN) {
         return; // sanity guard for absurd query areas
      }

      List<VoxelShape> barriers = null;
      for (int cx = minX; cx <= maxX; cx++) {
         for (int cz = minZ; cz <= maxZ; cz++) {
            if (!BorderColumnIndex.hasBorder(world, cx, cz)) {
               continue;
            }
            if (BorderGate.mayPassColumn(world, cx, cz, entity)) {
               continue;
            }
            if (barriers == null) {
               barriers = new ArrayList<>(4);
            }
            barriers.add(VoxelShapes.cuboid(
               (double) cx, (double) world.getBottomY(),
               (double) cz, (double) cx + 1.0, (double) world.getTopY(), (double) cz + 1.0
            ));
         }
      }
      if (barriers == null) {
         return;
      }
      List<VoxelShape> merged = new ArrayList<>(cir.getReturnValue().size() + barriers.size());
      merged.addAll(cir.getReturnValue());
      merged.addAll(barriers);
      cir.setReturnValue(merged);
   }
}
