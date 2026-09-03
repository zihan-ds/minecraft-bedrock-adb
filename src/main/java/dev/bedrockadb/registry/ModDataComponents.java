package dev.bedrockadb.registry;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModDataComponents {
   public static final ComponentType<List<String>> WHITELIST = Registry.register(
      Registries.DATA_COMPONENT_TYPE,
      Identifier.of("bedrock_adb", "whitelist"),
      ComponentType.<List<String>>builder().codec(Codec.STRING.listOf()).build()
   );

   private ModDataComponents() {
   }

   public static void register() {
   }
}
