package dev.bedrockadb.item;

import dev.bedrockadb.block.entity.UtilityBlockEntity;
import dev.bedrockadb.registry.ModDataComponents;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UtilityBlockItem extends BlockItem {
   public UtilityBlockItem(Block block, Settings settings) {
      super(block, settings);
   }

   public boolean postPlacement(BlockPos pos, World world, PlayerEntity player, ItemStack stack, BlockState state) {
      boolean result = super.postPlacement(pos, world, player, stack, state);
      if (!world.isClient && world.getBlockEntity(pos) instanceof UtilityBlockEntity blockEntity) {
         List<String> whitelist = (List<String>)stack.get(ModDataComponents.WHITELIST);
         if (whitelist != null) {
            blockEntity.setWhitelist(whitelist);
         }
      }

      return result;
   }
}
