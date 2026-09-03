package dev.bedrockadb.registry;

import dev.bedrockadb.block.AllowDenyBlock;
import dev.bedrockadb.block.BorderBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
   public static final Block ALLOW_BLOCK = new AllowDenyBlock(settings(MapColor.WHITE));
   public static final Block DENY_BLOCK = new AllowDenyBlock(settings(MapColor.BLACK));
   public static final Block BORDER_BLOCK = new BorderBlock(settings(MapColor.ORANGE));

   private ModBlocks() {
   }

   private static Settings settings(MapColor color) {
      return Settings.create().mapColor(color).strength(0.2F, 1200.0F).sounds(BlockSoundGroup.STONE).pistonBehavior(PistonBehavior.BLOCK);
   }

   public static void register() {
      Registry.register(Registries.BLOCK, Identifier.of("bedrock_adb", "allow_block"), ALLOW_BLOCK);
      Registry.register(Registries.BLOCK, Identifier.of("bedrock_adb", "deny_block"), DENY_BLOCK);
      Registry.register(Registries.BLOCK, Identifier.of("bedrock_adb", "border_block"), BORDER_BLOCK);
   }
}
