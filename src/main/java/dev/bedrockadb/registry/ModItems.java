package dev.bedrockadb.registry;

import dev.bedrockadb.item.UtilityBlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModItems {
   public static final Item ALLOW_BLOCK_ITEM = new UtilityBlockItem(ModBlocks.ALLOW_BLOCK, new Settings());
   public static final Item DENY_BLOCK_ITEM = new UtilityBlockItem(ModBlocks.DENY_BLOCK, new Settings());
   public static final Item BORDER_BLOCK_ITEM = new UtilityBlockItem(ModBlocks.BORDER_BLOCK, new Settings());

   private ModItems() {
   }

   public static void register() {
      Registry.register(Registries.ITEM, Identifier.of("bedrock_adb", "allow_block"), ALLOW_BLOCK_ITEM);
      Registry.register(Registries.ITEM, Identifier.of("bedrock_adb", "deny_block"), DENY_BLOCK_ITEM);
      Registry.register(Registries.ITEM, Identifier.of("bedrock_adb", "border_block"), BORDER_BLOCK_ITEM);
   }
}
