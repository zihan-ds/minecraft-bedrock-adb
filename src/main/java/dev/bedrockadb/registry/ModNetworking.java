package dev.bedrockadb.registry;

import dev.bedrockadb.config.ModConfig;
import dev.bedrockadb.net.WhitelistSyncPayload;
import dev.bedrockadb.state.WhitelistPersistentState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.Join;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ModNetworking {
   private ModNetworking() {
   }

   public static void registerPayloads() {
      PayloadTypeRegistry.playS2C().register(WhitelistSyncPayload.ID, WhitelistSyncPayload.CODEC);
      ServerPlayConnectionEvents.JOIN.register((Join)(handler, sender, server) -> sendSync(server, handler.player));
   }

   public static void sendSync(MinecraftServer server) {
      for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
         sendSync(server, player);
      }
   }

   public static void sendSync(MinecraftServer server, ServerPlayerEntity player) {
      WhitelistPersistentState state = WhitelistPersistentState.get(server.getOverworld());
      WhitelistSyncPayload payload = new WhitelistSyncPayload(
         state.list(),
         ModConfig.get().requireCreative(),
         ModConfig.get().opBypass(),
         ModConfig.get().showBorderEffect(),
         ModConfig.get().borderBlockMode()
      );
      ServerPlayNetworking.send(player, payload);
   }
}
