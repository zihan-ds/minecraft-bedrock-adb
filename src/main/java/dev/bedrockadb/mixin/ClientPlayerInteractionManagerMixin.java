package dev.bedrockadb.mixin;

import dev.bedrockadb.BedrockAdbClient;
import dev.bedrockadb.rule.RuleChecker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerInteractionManager.class})
public abstract class ClientPlayerInteractionManagerMixin {
   @Inject(
      method = {"breakBlock"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void bedrockAdb$denyBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
      if (isDenied(pos)) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"updateBlockBreakingProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void bedrockAdb$denyDig(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
      if (isDenied(pos)) {
         cir.setReturnValue(false);
      }
   }

   private static boolean isDenied(BlockPos pos) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world == null || client.player == null) {
         return false;
      } else if (RuleChecker.deniesBreaking(client.world, pos, client.player)) {
         BedrockAdbClient.sendDenied(client.player);
         return true;
      } else {
         return false;
      }
   }
}
