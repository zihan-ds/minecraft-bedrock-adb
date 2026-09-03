package dev.bedrockadb.mixin;

import dev.bedrockadb.registry.ModBlocks;
import dev.bedrockadb.state.BorderColumnIndex;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Keeps {@link BorderColumnIndex} in sync whenever a border block is placed or
 * removed through any means (commands, structure loading, players, ...). This
 * runs on both logical sides inside the same physical client process, so the
 * integrated-server side and the client side each maintain their own index
 * from the same block states.
 */
@Mixin(World.class)
public abstract class WorldBorderIndexMixin {
   @Inject(
      method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z",
      at = @At("TAIL")
   )
   private void bedrockAdb$trackBorderColumn2(BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
      track(pos, state);
   }

   @Inject(
      method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
      at = @At("TAIL")
   )
   private void bedrockAdb$trackBorderColumn3(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
      track(pos, state);
   }

   @Inject(
      method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z",
      at = @At("TAIL")
   )
   private void bedrockAdb$trackBorderColumn4(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir) {
      track(pos, state);
   }

   private void track(BlockPos pos, BlockState state) {
      if (pos == null || state == null) {
         return;
      }
      World world = (World) (Object) this;
      if (state.isOf(ModBlocks.BORDER_BLOCK)) {
         BorderColumnIndex.onBorderPlaced(world, pos);
      } else {
         BorderColumnIndex.onBorderRemoved(world, pos);
      }
   }
}
