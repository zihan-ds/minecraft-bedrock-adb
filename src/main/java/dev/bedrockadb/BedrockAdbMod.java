package dev.bedrockadb;

import dev.bedrockadb.config.ModConfig;
import dev.bedrockadb.registry.ModBlockEntities;
import dev.bedrockadb.registry.ModBlocks;
import dev.bedrockadb.registry.ModCommands;
import dev.bedrockadb.registry.ModDataComponents;
import dev.bedrockadb.registry.ModItemGroups;
import dev.bedrockadb.registry.ModItems;
import dev.bedrockadb.registry.ModNetworking;
import dev.bedrockadb.rule.RuleChecker;
import dev.bedrockadb.state.BorderColumnIndex;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedrockAdbMod implements ModInitializer {
   public static final String MOD_ID = "bedrock_adb";
   public static final Logger LOGGER = LoggerFactory.getLogger("bedrock_adb");

   public void onInitialize() {
      ModConfig.load();
      ModDataComponents.register();
      ModBlocks.register();
      ModItems.register();
      ModBlockEntities.register();
      ModItemGroups.register();
      ModNetworking.registerPayloads();
      ModCommands.register();
      registerEvents();
      LOGGER.info("[bedrock_adb] initialized (client mod, works in single-player and LAN co-op)");
   }

   private static void registerEvents() {
      UseBlockCallback.EVENT.register(BedrockAdbMod::onUseBlockServer);
      PlayerBlockBreakEvents.BEFORE.register(BedrockAdbMod::onBreakBlockServer);
      // Border column index: the integrated-server side maintains its own view
      // of loaded border blocks from chunk (un)load events.
      ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> BorderColumnIndex.onChunkLoad(world, chunk));
      ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> BorderColumnIndex.onChunkUnload(world, chunk));
   }

   private static ActionResult onUseBlockServer(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
      if (!world.isClient && hand == Hand.MAIN_HAND) {
         ItemStack stack = player.getStackInHand(hand);
         if (stack.isEmpty()) {
            return ActionResult.PASS;
         } else if (!(stack.getItem() instanceof BlockItem) && !(stack.getItem() instanceof BucketItem)) {
            return ActionResult.PASS;
         } else {
            BlockPos target = hitResult.getBlockPos().offset(hitResult.getSide());
            return RuleChecker.deniesPlacement(world, target, player, stack) ? ActionResult.FAIL : ActionResult.PASS;
         }
      } else {
         return ActionResult.PASS;
      }
   }

   private static boolean onBreakBlockServer(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
      return world.isClient ? true : !RuleChecker.deniesBreaking(world, pos, player);
   }
}
