package dev.bedrockadb.block;

import dev.bedrockadb.registry.ModBlockEntities;
import dev.bedrockadb.rule.BedrockUtilityBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public abstract class UtilityBlock extends Block implements BlockEntityProvider, BedrockUtilityBlock {
   public UtilityBlock(Settings settings) {
      super(settings);
   }

   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return ModBlockEntities.UTILITY.instantiate(pos, state);
   }
}
