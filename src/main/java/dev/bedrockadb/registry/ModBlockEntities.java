package dev.bedrockadb.registry;

import dev.bedrockadb.block.entity.UtilityBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.Builder;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
   public static final BlockEntityType<UtilityBlockEntity> UTILITY = Builder.create(
         UtilityBlockEntity::new, new Block[]{ModBlocks.ALLOW_BLOCK, ModBlocks.DENY_BLOCK, ModBlocks.BORDER_BLOCK}
      )
      .build();

   private ModBlockEntities() {
   }

   public static void register() {
      Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("bedrock_adb", "utility"), UTILITY);
   }
}
