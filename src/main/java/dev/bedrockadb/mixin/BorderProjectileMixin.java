package dev.bedrockadb.mixin;

import dev.bedrockadb.rule.BorderGate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Arrows, spectral arrows and thrown tridents fly on their own raycast-based
 * path (not the voxel movement pipeline), so the column barrier is enforced
 * here: the tick start position is captured and, if the projectile crossed a
 * border column during the tick while it must be stopped, it is clamped back
 * just in front of the wall with zero velocity.
 *
 * <p>Note on {@code method = "method_5773"}: {@code tick} is mapped only on
 * {@code Entity} in the Yarn mappings, so the compile-time static mixin remap
 * cannot resolve it on these subclasses; every class in this virtual chain
 * implements it under the shared production name {@code method_5773}. This
 * makes the target correct in the produced (intermediary) jar.
 */
@Mixin(PersistentProjectileEntity.class)
public abstract class BorderProjectileMixin {
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
