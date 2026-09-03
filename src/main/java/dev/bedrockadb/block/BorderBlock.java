package dev.bedrockadb.block;

import dev.bedrockadb.registry.ModBlockEntities;
import dev.bedrockadb.rule.BedrockUtilityBlock;
import dev.bedrockadb.rule.BorderGate;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WallBlock;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BorderBlock extends WallBlock implements BlockEntityProvider, BedrockUtilityBlock {
   private static final VoxelShape WALL_SHAPE = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 1.5, 1.0);

   public BorderBlock(Settings settings) {
      super(settings);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return ModBlockEntities.UTILITY.instantiate(pos, state);
   }

   public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      if (context instanceof EntityShapeContext entityContext) {
         Entity entity = entityContext.getEntity();
         if (entity != null && passesThrough(world, pos, entity)) {
            return VoxelShapes.empty();
         }
      }

      return WALL_SHAPE;
   }

   private static boolean passesThrough(BlockView world, BlockPos pos, Entity entity) {
      if (world instanceof World w) {
         // Cell-level rule of the border block itself. The whole-column barrier
         // (any Y, over/under the block) is injected by the entity collision mixin.
         return BorderGate.mayPassCell(w, pos, entity);
      }

      // Views that are not a world (rare edge cases): keep the classic rules.
      if (entity instanceof PlayerEntity player) {
         return player.isSpectator();
      }
      return entity instanceof ProjectileEntity || entity instanceof VexEntity || entity instanceof EnderDragonEntity;
   }
}
