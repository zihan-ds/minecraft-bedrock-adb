package dev.bedrockadb.rule;

import dev.bedrockadb.config.BorderBlockMode;
import dev.bedrockadb.config.ModConfig;
import dev.bedrockadb.state.ClientSyncState;
import dev.bedrockadb.state.WhitelistPersistentState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public final class WhitelistSources {
   private WhitelistSources() {
   }

   public static WhitelistSource forWorld(World world) {
      return (WhitelistSource)(!world.isClient && world instanceof ServerWorld serverWorld
         ? new WhitelistSources.ServerSource(serverWorld)
         : ClientSyncState.INSTANCE);
   }

   public static final class ServerSource implements WhitelistSource {
      private final ServerWorld world;

      private ServerSource(ServerWorld world) {
         this.world = world;
      }

      @Override
      public boolean isWhitelisted(String name) {
         return WhitelistPersistentState.get(this.world).isWhitelisted(name);
      }

      @Override
      public boolean requireCreative() {
         return ModConfig.get().requireCreative();
      }

      @Override
      public boolean opBypass() {
         return ModConfig.get().opBypass();
      }

      @Override
      public BorderBlockMode borderBlockMode() {
         return ModConfig.get().borderBlockMode();
      }
   }
}
