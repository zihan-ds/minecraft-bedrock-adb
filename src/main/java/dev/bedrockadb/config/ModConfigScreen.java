package dev.bedrockadb.config;

import dev.bedrockadb.registry.ModNetworking;
import java.util.Locale;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.text.Text;

/**
 * Cloth Config screen for every option of {@link ModConfig}.
 *
 * <p>The screen can only be opened by the host (the player who started the
 * single-player/LAN world). Changes are written to {@code config/bedrock-adb.json}
 * and pushed to every connected LAN player through {@link ModNetworking}.
 */
public final class ModConfigScreen {
   private ModConfigScreen() {
   }

   public static Screen create(Screen parent) {
      ModConfig config = ModConfig.get();

      ConfigBuilder builder = ConfigBuilder.create()
         .setParentScreen(parent)
         .setTitle(Text.translatable("config.bedrock_adb.title"))
         .setSavingRunnable(ModConfigScreen::saveAndSync);

      ConfigEntryBuilder entryBuilder = builder.entryBuilder();
      ConfigCategory general = builder.getOrCreateCategory(Text.translatable("config.bedrock_adb.category.general"));

      general.addEntry(entryBuilder.startEnumSelector(
            Text.translatable("option.bedrock_adb.border_mode"), BorderBlockMode.class, config.borderBlockMode())
         .setDefaultValue(BorderBlockMode.BEDROCK_LIKE)
         .setEnumNameProvider(ModConfigScreen::displayName)
         .setSaveConsumer(config::setBorderBlockMode)
         .setTooltip(
            Text.translatable("option.bedrock_adb.border_mode.tooltip.1"),
            Text.translatable("option.bedrock_adb.border_mode.tooltip.2"),
            Text.translatable("option.bedrock_adb.border_mode.tooltip.3")
         )
         .build());

      general.addEntry(entryBuilder.startBooleanToggle(
            Text.translatable("option.bedrock_adb.require_creative"), config.requireCreative())
         .setDefaultValue(true)
         .setSaveConsumer(config::setRequireCreative)
         .setTooltip(Text.translatable("option.bedrock_adb.require_creative.tooltip"))
         .build());

      general.addEntry(entryBuilder.startBooleanToggle(
            Text.translatable("option.bedrock_adb.op_bypass"), config.opBypass())
         .setDefaultValue(true)
         .setSaveConsumer(config::setOpBypass)
         .setTooltip(Text.translatable("option.bedrock_adb.op_bypass.tooltip"))
         .build());

      general.addEntry(entryBuilder.startBooleanToggle(
            Text.translatable("option.bedrock_adb.show_border_effect"), config.showBorderEffect())
         .setDefaultValue(true)
         .setSaveConsumer(config::setShowBorderEffect)
         .setTooltip(Text.translatable("option.bedrock_adb.show_border_effect.tooltip"))
         .build());

      return builder.build();
   }

   private static Text displayName(Enum<?> value) {
      return Text.translatable("option.bedrock_adb.border_mode." + ((BorderBlockMode) value).name().toLowerCase(Locale.ROOT));
   }

   private static void saveAndSync() {
      ModConfig.save();
      MinecraftClient client = MinecraftClient.getInstance();
      if (client != null && client.getServer() instanceof IntegratedServer server) {
         // Push the new settings to every connected LAN player.
         ModNetworking.sendSync(server);
      }
   }
}
