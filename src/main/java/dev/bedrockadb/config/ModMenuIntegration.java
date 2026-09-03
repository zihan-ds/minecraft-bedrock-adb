package dev.bedrockadb.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Mod Menu integration: registers the Cloth Config screen behind the
 * "Config" button of Mod Menu.
 *
 * <p>Both Mod Menu and Cloth Config are optional at runtime. When Cloth Config
 * is missing the factory is {@code null}, so Mod Menu simply hides the config
 * button and the mod keeps working (the /allowdeny config commands remain
 * available).
 */
public class ModMenuIntegration implements ModMenuApi {
   @Override
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      if (!FabricLoader.getInstance().isModLoaded("cloth-config")) {
         return null;
      }
      return ModConfigScreen::create;
   }
}
