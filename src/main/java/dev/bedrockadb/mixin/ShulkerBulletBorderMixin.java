package dev.bedrockadb.mixin;

import dev.bedrockadb.rule.BorderGate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Shulker bullets steer themselves; enforce the border column barrier around
 * their tick like for arrows.
 */
@Mixin(ShulkerBulletEntity.class)
public abstract class ShulkerBulletBorderMixin {
   @Unique
   private Vec3d bedrockAdb$tickStart;

   @Inject(method = "method_5773", at = @At("HEAD"))
   private void bedrockAdb$captureStart(CallbackInfo ci) {
      this.bedrockAdb$tickStart = new Vec3d(((Entity) (Object) this).getX(), ((Entity) (Object) this).getY(), ((Entity) (Object) this).getZ());
   }

   @Inject(method = "method_5773", at = @At("TAIL"))
   private void bedrockAdb$clampBorderCrossing(CallbackInfo ci) {
      Vec3d start = this.bedrockAdb$tickStart;
      this.bedrockAdb$tickStart = null;
      if (start != null) {
         Entity self = (Entity) (Object) this;
         if (self.getWorld() instanceof World world) {
            BorderGate.clampProjectileStep(world, self, start);
         }
      }
   }
}
