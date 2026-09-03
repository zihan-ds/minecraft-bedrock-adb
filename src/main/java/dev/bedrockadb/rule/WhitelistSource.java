package dev.bedrockadb.rule;

import dev.bedrockadb.config.BorderBlockMode;

public interface WhitelistSource {
   boolean isWhitelisted(String var1);

   boolean requireCreative();

   boolean opBypass();

   BorderBlockMode borderBlockMode();
}
