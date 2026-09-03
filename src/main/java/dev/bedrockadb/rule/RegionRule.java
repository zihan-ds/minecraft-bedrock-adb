package dev.bedrockadb.rule;

import dev.bedrockadb.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class RegionRule {
   private final BlockPos pos;
   private final boolean allows;

   private RegionRule(BlockPos pos, boolean allows) {
      this.pos = pos;
      this.allows = allows;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean allows() {
      return this.allows;
   }

   public static RegionRule findBelow(World world, BlockPos pos) {
      int x = pos.getX();
      int z = pos.getZ();

      for (int y = pos.getY() - 1; y >= world.getBottomY(); y--) {
         Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
         if (block == ModBlocks.ALLOW_BLOCK) {
            return new RegionRule(new BlockPos(x, y, z), true);
         }

         if (block == ModBlocks.DENY_BLOCK) {
            return new RegionRule(new BlockPos(x, y, z), false);
         }
      }

      return null;
   }
}
