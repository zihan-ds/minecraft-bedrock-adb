package dev.bedrockadb.net;

import dev.bedrockadb.config.BorderBlockMode;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.CustomPayload.Id;
import net.minecraft.util.Identifier;

public record WhitelistSyncPayload(
   List<String> whitelist,
   boolean requireCreative,
   boolean opBypass,
   boolean showBorderEffect,
   BorderBlockMode borderBlockMode
) implements CustomPayload {
   public static final Id<WhitelistSyncPayload> ID = new Id<>(Identifier.of("bedrock_adb", "whitelist_sync"));
   public static final PacketCodec<PacketByteBuf, WhitelistSyncPayload> CODEC = PacketCodec.tuple(
      PacketCodecs.STRING.collect(PacketCodecs.toList()),
      WhitelistSyncPayload::whitelist,
      PacketCodecs.BOOL,
      WhitelistSyncPayload::requireCreative,
      PacketCodecs.BOOL,
      WhitelistSyncPayload::opBypass,
      PacketCodecs.BOOL,
      WhitelistSyncPayload::showBorderEffect,
      PacketCodecs.STRING,
      payload -> payload.borderBlockMode().name(),
      WhitelistSyncPayload::new
   );

   private static BorderBlockMode parseMode(String name) {
      try {
         return BorderBlockMode.valueOf(name.toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException ignored) {
         return BorderBlockMode.BEDROCK_LIKE; // unknown value from an older/newer peer
      }
   }

   public WhitelistSyncPayload(
      List<String> whitelist,
      boolean requireCreative,
      boolean opBypass,
      boolean showBorderEffect,
      String borderBlockMode
   ) {
      this(whitelist, requireCreative, opBypass, showBorderEffect, parseMode(borderBlockMode));
   }

   public Id<? extends CustomPayload> getId() {
      return ID;
   }
}
