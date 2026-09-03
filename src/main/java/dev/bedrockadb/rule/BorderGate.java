package dev.bedrockadb.rule;

import dev.bedrockadb.block.entity.UtilityBlockEntity;
import dev.bedrockadb.config.BorderBlockMode;
import dev.bedrockadb.state.BorderColumnIndex;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Shared pass-through rules for {@code bedrock_adb:border_block}.
 *
 * <p>Two entry points exist because the barrier is evaluated on two levels:
 * <ul>
 *   <li>{@link #mayPassCell} - the border block's own cell (its real collision
 *       shape and other cell-level queries);</li>
 *   <li>{@link #mayPassColumn} - the virtual whole-column barrier used by the
 *       collision mixin, so the column cannot be crossed over or under at any Y.</li>
 * </ul>
 * Both use identical rules: spectators pass, privileged players (global
 * whitelist or OP + creative per the config) pass, per-block whitelisted
 * players pass; non-player entities pass only in Bedrock-like mode if they are
 * projectiles, vex or the ender dragon.
 */
public final class BorderGate {
   private BorderGate() {
   }

   /** Single border block cell rule (used by {@code BorderBlock}). */
   public static boolean mayPassCell(World world, BlockPos pos, Entity entity) {
      if (entity instanceof PlayerEntity player) {
         if (player.isSpectator()) {
            return true;
         }
         if (Privilege.isPrivileged(player, WhitelistSources.forWorld(world))) {
            return true;
         }
         return blockEntityWhitelisted(world, pos, player);
      }
      return classicExemptions(world, entity);
   }

   /** Whole-column rule (virtual barrier at any Y). */
   public static boolean mayPassColumn(World world, int x, int z, Entity entity) {
      if (entity instanceof PlayerEntity player) {
         if (player.isSpectator()) {
            return true;
         }
         if (Privilege.isPrivileged(player, WhitelistSources.forWorld(world))) {
            return true;
         }
         for (int y : BorderColumnIndex.borderYs(world, x, z)) {
            if (blockEntityWhitelisted(world, new BlockPos(x, y, z), player)) {
               return true;
            }
         }
         return false;
      }
      return classicExemptions(world, entity);
   }

   /**
    * Whether a projectile that is about to cross a border column must be stopped.
    * In Bedrock-like mode projectiles always pass. In strict mode they are
    * stopped unless fired by a privileged player.
    */
   public static boolean mustStopProjectile(World world, Entity projectile) {
      if (!modeOf(world).blocksAllEntities()) {
         return false;
      }
      if (projectile instanceof ProjectileEntity projectileEntity) {
         Entity owner = projectileEntity.getOwner();
         if (owner instanceof PlayerEntity player && Privilege.isPrivileged(player, WhitelistSources.forWorld(world))) {
            return false;
         }
      }
      return true;
   }

   /**
    * Clamps a projectile that crossed a border column between {@code from} and
    * its current position during one tick: it is placed just before the column
    * face and its velocity is zeroed, so it visibly stops at the invisible wall.
    */
   public static void clampProjectileStep(World world, Entity projectile, Vec3d from) {
      if (from == null || !mustStopProjectile(world, projectile)) {
         return;
      }
      double x0 = from.x;
      double z0 = from.z;
      double dx = projectile.getX() - x0;
      double dz = projectile.getZ() - z0;
      double span = Math.max(Math.abs(dx), Math.abs(dz));
      if (span <= 1.0E-6) {
         return;
      }
      boolean startBlocked = BorderColumnIndex.hasBorder(world, (int) Math.floor(x0), (int) Math.floor(z0));
      int steps = Math.min(Math.max(8, (int) Math.ceil(span * 8.0)), 256);
      for (int i = 1; i <= steps; i++) {
         double t = (double) i / (double) steps;
         int cx = (int) Math.floor(x0 + dx * t);
         int cz = (int) Math.floor(z0 + dz * t);
         if (!startBlocked && BorderColumnIndex.hasBorder(world, cx, cz)) {
            // Back off half a sampling step so the projectile rests just outside the wall.
            double tBack = Math.max(0.0, (double) (i - 1) / (double) steps - 0.5 / (double) steps);
            projectile.setPosition(x0 + dx * tBack, projectile.getY(), z0 + dz * tBack);
            projectile.setVelocity(Vec3d.ZERO);
            return;
         }
      }
   }

   private static boolean classicExemptions(World world, Entity entity) {
      if (modeOf(world).blocksAllEntities()) {
         return false; // strict mode: no non-player exemptions
      }
      return entity instanceof ProjectileEntity || entity instanceof VexEntity || entity instanceof EnderDragonEntity;
   }

   private static boolean blockEntityWhitelisted(World world, BlockPos pos, PlayerEntity player) {
      BlockEntity entity = world.getBlockEntity(pos);
      return entity instanceof UtilityBlockEntity utility && utility.isWhitelisted(player.getGameProfile().getName());
   }

   private static BorderBlockMode modeOf(World world) {
      return WhitelistSources.forWorld(world).borderBlockMode();
   }
}
