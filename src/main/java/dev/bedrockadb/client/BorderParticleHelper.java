package dev.bedrockadb.client;

import dev.bedrockadb.registry.ModBlocks;
import dev.bedrockadb.state.ClientSyncState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3f;

public final class BorderParticleHelper {
   private static final Vector3f RED = new Vector3f(0.9F, 0.25F, 0.15F);
   private static final int RADIUS = 12;
   private static final int VERTICAL_RANGE = 4;
   private static final int INTERVAL = 8;
   private static final int MAX_BLOCKS = 24;
   private static int tickCount;

   private BorderParticleHelper() {
   }

   public static void tick() {
      tickCount++;
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.world != null && client.player != null) {
         if (ClientSyncState.INSTANCE.showBorderEffect()) {
            if (tickCount % 8 == 0) {
               BlockPos center = client.player.getBlockPos();
               int spawned = 0;

               for (int dx = -12; dx <= 12 && spawned < 24; dx++) {
                  for (int dz = -12; dz <= 12 && spawned < 24; dz++) {
                     for (int dy = -4; dy <= 4 && spawned < 24; dy++) {
                        BlockPos pos = center.add(dx, dy, dz);
                        if (client.world.getBlockState(pos).isOf(ModBlocks.BORDER_BLOCK)) {
                           spawnParticles(client, pos);
                           spawned++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void spawnParticles(MinecraftClient client, BlockPos pos) {
      double x = (double)pos.getX() + 0.5;
      double z = (double)pos.getZ() + 0.5;
      client.world
         .addParticle(new DustParticleEffect(RED, 1.0F), true, x, (double)pos.getY() + 1.06, z, (Math.random() - 0.5) * 0.1, 0.08, (Math.random() - 0.5) * 0.1);
      client.world
         .addParticle(new DustParticleEffect(RED, 1.0F), true, x, (double)pos.getY() - 0.06, z, (Math.random() - 0.5) * 0.1, -0.08, (Math.random() - 0.5) * 0.1);
   }
}
