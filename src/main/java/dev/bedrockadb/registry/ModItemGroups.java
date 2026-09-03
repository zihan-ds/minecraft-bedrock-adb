package dev.bedrockadb.registry;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
   private ModItemGroups() {
   }

   public static void register() {
      Registry.register(
         Registries.ITEM_GROUP,
         Identifier.of("bedrock_adb", "main"),
         FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup.bedrock_adb"))
            .icon(() -> new ItemStack(ModItems.ALLOW_BLOCK_ITEM))
            .entries((context, entries) -> {
               entries.add(ModItems.ALLOW_BLOCK_ITEM);
               entries.add(ModItems.DENY_BLOCK_ITEM);
               entries.add(ModItems.BORDER_BLOCK_ITEM);
            })
            .build()
      );
   }
}
