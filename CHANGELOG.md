# Changelog

Version scheme: `{mod_version}+{mc_version}` since `1.2.1`; git tags use `v{mod_version}`.

## [1.2.1+1.21] — 2026-09-03

### Fixed
- Border blocks now block the **entire (x, z) column at every Y** (world bottom
  to top): entities that are not allowed to pass can no longer cross the wall by
  jumping over it, flying/elytra above it, or tunnelling underneath it.
  Implemented as a virtual full-height column barrier (per-world border column
  index + collision-stream injection), keeping all privilege rules intact.
- In "Block all entities" mode, high-flying projectiles (arrows, spectral
  arrows, tridents, ender pearls, snowballs/eggs, fireballs, shulker bullets,
  ...) are stopped at the border column as well; projectiles fired by
  privileged players still pass.
- Per-block NBT whitelist now applies to the whole column the block belongs to
  (any border of that column can open it).

### Notes
- Version scheme changed to `{mod_version}+{mc_version}` from this release on.

## [1.2.0] — 2026-09-03

### Added
- Selectable **border block mode**: `bedrock_like` (default, previous
  behaviour) or `block_all_entities` (everything blocked except privileged
  players and spectators).
- Visual configuration screen (Cloth Config) exposed through Mod Menu, plus
  new translation keys (zh_cn/en_us); both libraries optional at runtime.
- `/allowdeny config borderMode bedrock_like|block_all_entities` command.
- Config changes (any source) are pushed live to LAN clients.

## [1.1.3] — baseline

The last release of the original codebase (jar-only). The source tree in this
repository was reconstructed from that jar and then extended into 1.2.x.
