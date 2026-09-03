package dev.bedrockadb.rule;

import dev.bedrockadb.block.entity.UtilityBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class RuleChecker {
   private RuleChecker() {
   }

   public static boolean deniesPlacement(World world, BlockPos pos, PlayerEntity player, ItemStack stack) {
      WhitelistSource source = WhitelistSources.forWorld(world);
      if (Privilege.isPrivileged(player, source)) {
         return false;
      } else {
         if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BedrockUtilityBlock) {
            return true;
         }

         return isDeniedByColumn(world, pos, player, source);
      }
   }

   public static boolean deniesBreaking(World world, BlockPos pos, PlayerEntity player) {
      WhitelistSource source = WhitelistSources.forWorld(world);
      if (Privilege.isPrivileged(player, source)) {
         return false;
      } else {
         return world.getBlockState(pos).getBlock() instanceof BedrockUtilityBlock ? true : isDeniedByColumn(world, pos, player, source);
      }
   }

   private static boolean isDeniedByColumn(World world, BlockPos pos, PlayerEntity player, WhitelistSource source) {
      RegionRule rule = RegionRule.findBelow(world, pos);
      if (rule != null && !rule.allows()) {
         if (world.getBlockEntity(rule.pos()) instanceof UtilityBlockEntity blockEntity && blockEntity.isWhitelisted(player.getGameProfile().getName())) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }
}
