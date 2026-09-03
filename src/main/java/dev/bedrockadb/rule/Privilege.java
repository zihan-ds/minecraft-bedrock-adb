package dev.bedrockadb.rule;

import net.minecraft.entity.player.PlayerEntity;

public final class Privilege {
   private Privilege() {
   }

   public static boolean isPrivileged(PlayerEntity player, WhitelistSource source) {
      String name = player.getGameProfile().getName();
      if (source.isWhitelisted(name)) {
         return true;
      } else if (!source.opBypass()) {
         return false;
      } else {
         return !source.requireCreative() ? player.hasPermissionLevel(2) : player.isCreative() && player.hasPermissionLevel(2);
      }
   }
}
