package dev.bedrockadb;

import dev.bedrockadb.client.BorderParticleHelper;
import dev.bedrockadb.net.WhitelistSyncPayload;
import dev.bedrockadb.rule.RuleChecker;
import dev.bedrockadb.state.BorderColumnIndex;
import dev.bedrockadb.state.ClientSyncState;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.Disconnect;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BedrockAdbClient implements ClientModInitializer {
   public void onInitializeClient() {
      ClientPlayNetworking.registerGlobalReceiver(
         WhitelistSyncPayload.ID, (payload, context) -> context.client().execute(() -> ClientSyncState.INSTANCE.update(payload))
      );
      ClientPlayConnectionEvents.DISCONNECT.register((Disconnect)(handler, client) -> ClientSyncState.INSTANCE.reset());
      UseBlockCallback.EVENT.register(BedrockAdbClient::onUseBlockClient);
      ClientTickEvents.END_CLIENT_TICK.register((EndTick)client -> BorderParticleHelper.tick());
      // Border column index: the client side maintains its own view of loaded
      // border blocks from chunk (un)load events (LAN guests included).
      ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> BorderColumnIndex.onChunkLoad(world, chunk));
      ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> BorderColumnIndex.onChunkUnload(world, chunk));
   }

   private static ActionResult onUseBlockClient(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
      if (world.isClient && hand == Hand.MAIN_HAND) {
         ItemStack stack = player.getStackInHand(hand);
         if (stack.isEmpty()) {
            return ActionResult.PASS;
         } else if (!(stack.getItem() instanceof BlockItem) && !(stack.getItem() instanceof BucketItem)) {
            return ActionResult.PASS;
         } else {
            BlockPos target = hitResult.getBlockPos().offset(hitResult.getSide());
            if (RuleChecker.deniesPlacement(world, target, player, stack)) {
               sendDenied(player);
               return ActionResult.FAIL;
            } else {
               return ActionResult.PASS;
            }
         }
      } else {
         return ActionResult.PASS;
      }
   }

   public static void sendDenied(PlayerEntity player) {
      player.sendMessage(Text.translatable("message.bedrock_adb.denied"), true);
      player.getWorld()
         .playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE, SoundCategory.BLOCKS, 0.6F, 1.4F);
   }
}
