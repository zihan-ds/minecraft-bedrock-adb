package dev.bedrockadb.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.bedrockadb.BedrockAdbMod;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

public final class ModConfig {
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("bedrock-adb.json");
   private static ModConfig instance = new ModConfig();
   private boolean requireCreative = true;
   private boolean opBypass = true;
   private boolean showBorderEffect = true;
   private BorderBlockMode borderBlockMode = BorderBlockMode.BEDROCK_LIKE;

   private ModConfig() {
   }

   public static ModConfig get() {
      return instance;
   }

   public static void load() {
      try {
         if (Files.exists(PATH)) {
            ModConfig parsed = GSON.fromJson(Files.readString(PATH), ModConfig.class);
            if (parsed != null) {
               // Older files or hand edits may carry an unknown/null mode: fall back safely.
               if (parsed.borderBlockMode == null) {
                  parsed.borderBlockMode = BorderBlockMode.BEDROCK_LIKE;
               }
               instance = parsed;
            }
         }
      } catch (Exception e) {
         BedrockAdbMod.LOGGER.warn("[bedrock_adb] failed to read config, using defaults", e);
         instance = new ModConfig();
      }

      save();
   }

   public static void save() {
      try {
         Files.createDirectories(PATH.getParent());
         Files.writeString(PATH, GSON.toJson(instance));
      } catch (Exception e) {
         BedrockAdbMod.LOGGER.warn("[bedrock_adb] failed to write config", e);
      }
   }

   public boolean requireCreative() {
      return this.requireCreative;
   }

   public boolean opBypass() {
      return this.opBypass;
   }

   public boolean showBorderEffect() {
      return this.showBorderEffect;
   }

   public BorderBlockMode borderBlockMode() {
      return this.borderBlockMode;
   }

   public void setRequireCreative(boolean value) {
      this.requireCreative = value;
   }

   public void setOpBypass(boolean value) {
      this.opBypass = value;
   }

   public void setShowBorderEffect(boolean value) {
      this.showBorderEffect = value;
   }

   public void setBorderBlockMode(BorderBlockMode value) {
      this.borderBlockMode = value == null ? BorderBlockMode.BEDROCK_LIKE : value;
   }
}
