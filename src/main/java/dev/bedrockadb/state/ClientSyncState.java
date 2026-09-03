package dev.bedrockadb.state;

import dev.bedrockadb.config.BorderBlockMode;
import dev.bedrockadb.net.WhitelistSyncPayload;
import dev.bedrockadb.rule.WhitelistSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ClientSyncState implements WhitelistSource {
   public static final ClientSyncState INSTANCE = new ClientSyncState();
   private final List<String> whitelist = new ArrayList<>();
   private boolean requireCreative = true;
   private boolean opBypass = true;
   private boolean showBorderEffect = true;
   private BorderBlockMode borderBlockMode = BorderBlockMode.BEDROCK_LIKE;

   private ClientSyncState() {
   }

   public void update(WhitelistSyncPayload payload) {
      this.whitelist.clear();
      this.whitelist.addAll(payload.whitelist());
      this.requireCreative = payload.requireCreative();
      this.opBypass = payload.opBypass();
      this.showBorderEffect = payload.showBorderEffect();
      this.borderBlockMode = payload.borderBlockMode();
   }

   public void reset() {
      this.whitelist.clear();
      this.requireCreative = true;
      this.opBypass = true;
      this.showBorderEffect = true;
      this.borderBlockMode = BorderBlockMode.BEDROCK_LIKE;
   }

   @Override
   public boolean isWhitelisted(String name) {
      return this.whitelist.contains(name.toLowerCase(Locale.ROOT));
   }

   @Override
   public boolean requireCreative() {
      return this.requireCreative;
   }

   @Override
   public boolean opBypass() {
      return this.opBypass;
   }

   @Override
   public BorderBlockMode borderBlockMode() {
      return this.borderBlockMode;
   }

   public boolean showBorderEffect() {
      return this.showBorderEffect;
   }
}
