package dev.bedrockadb.config;

/**
 * Selects how {@code bedrock_adb:border_block} treats entities that try to move
 * through it.
 *
 * <p>{@link #BEDROCK_LIKE} mirrors the Bedrock Edition behaviour (and the
 * behaviour shipped in 1.1.3): unprivileged players and monsters are blocked
 * while projectiles, spectators, privileged players and uncontrollable
 * entities (vex, ender dragon) may pass.
 *
 * <p>{@link #BLOCK_ALL_ENTITIES} is the strict mode: every entity is blocked
 * except privileged players (global whitelist, or OP + Creative per the
 * {@code requireCreative}/{@code opBypass} rules) and spectators.
 *
 * <p>Values are stored by name in the JSON config file.
 */
public enum BorderBlockMode {
   BEDROCK_LIKE,
   BLOCK_ALL_ENTITIES;

   public boolean blocksAllEntities() {
      return this == BLOCK_ALL_ENTITIES;
   }
}
