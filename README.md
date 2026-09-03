# Bedrock Allow Deny Border (bedrock_adb)

[![Build](https://github.com/YOUR_USERNAME/minecraft-bedrock-adb/actions/workflows/build.yml/badge.svg)](https://github.com/YOUR_USERNAME/minecraft-bedrock-adb/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

A **client-side Fabric mod for Minecraft Java 1.21** that faithfully ports the
Bedrock / Education Edition **Allow**, **Deny** and **Border** blocks to Java
Edition, designed for single-player and **LAN co-op** worldbuilding.

> All players on the LAN need the same mod version installed: the rules are
> evaluated on every client and the host synchronizes the config to everyone.

## Features

- **Allow / Deny blocks** — column rules: the nearest allow/deny block *below*
  a position governs block placement & breaking in that whole column (highest
  wins). Invisible-ish utility blocks, extremely blast resistant, not movable
  by pistons.
- **Border blocks** — invisible walls that block entities from **the entire
  (x, z) column at every Y** (world bottom to top): they cannot be crossed by
  jumping over, flying above, or tunnelling underneath.
- **Two border block modes** (configurable):
  | Who may pass | Bedrock-like (default) | Block all entities |
  |---|---|---|
  | Unprivileged players / monsters | ❌ blocked | ❌ blocked |
  | Privileged players (whitelist / OP+Creative) | ✅ pass | ✅ pass |
  | Spectators | ✅ pass | ✅ pass |
  | Projectiles (arrows, pearls, ...) | ✅ pass | ❌ blocked |
  | Vex, Ender Dragon | ✅ pass | ❌ blocked |
- **Privilege model** — global whitelist (`/allowdeny whitelist …`), per-block
  NBT whitelist, or OP (permission level 2, optionally requiring Creative).
- **Visual config GUI** — Mod Menu "Config" button backed by a Cloth Config
  screen (both optional at runtime; the `/allowdeny config …` commands always
  work).
- Config changes made by the host are **pushed live to every LAN player**.

## Requirements

| Item | Version |
|---|---|
| Minecraft | Java 1.21 |
| Fabric Loader | ≥ 0.19.4 |
| Fabric API | any 1.21 build |
| Mod Menu (optional) | 11.0.x |
| Cloth Config (optional) | 15.0.140 |

> Without Mod Menu + Cloth Config there is no config button; configure with the
> in-game commands instead (see below).

## Quick start

1. Install Fabric Loader for 1.21, drop `bedrock-adb-<version>.jar` (plus
   Fabric API) into `mods/`.
2. Get the blocks from the **Bedrock Allow / Deny / Border** creative tab, or
   with `/give @s bedrock_adb:allow_block` etc.
3. Open config via Mods screen → **Bedrock Allow Deny Border** → **Config**, or
   use commands (OP level 2):

```
/allowdeny config borderMode bedrock_like|block_all_entities
/allowdeny config requireCreative <true|false>
/allowdeny config opBypass <true|false>
/allowdeny config showBorderEffect <true|false>
/allowdeny whitelist add|remove|list|clear <player>
/allowdeny reload
```

A full Chinese user manual lives in [`使用说明.md`](使用说明.md).

## Building from source

JDK 21+ and network access are required.

```bash
./gradlew build          # or gradlew.bat build on Windows
# artifact: build/libs/bedrock-adb-<version>.jar
```

Toolchain: Minecraft 1.21 · Yarn `1.21+build.9` · Fabric Loom `1.17.20` ·
Fabric Loader `0.19.5` · Fabric API `0.102.0+1.21`.

## Versioning & releases

- Version scheme: `{mod_version}+{mc_version}` (e.g. `1.2.1+1.21`), git tags
  `v1.2.1`.
- Pushing a `v*` tag runs CI which attaches the jar to a GitHub Release and —
  once `MODRINTH_TOKEN` is configured as a repository secret — publishes it to
  Modrinth automatically. See `CHANGELOG.md` for release notes.

## Credits

Authored and maintained by **zihan_ds** (`bedrock-adb`). This repository's
source tree was reconstructed from the original `1.1.3` jar and re-developed
into `1.2.x` (border modes, whole-column barrier, config GUI). See
[`CREDITS.md`](CREDITS.md).

## License

[MIT](LICENSE) © 2026 zihan_ds (bedrock-adb)

---

## 中文简介

**Bedrock Allow Deny Border（bedrock_adb）** 是 Minecraft Java 1.21 的 Fabric
**客户端** Mod：在 Java 版中复刻基岩版教学用的**允许方块 / 拒绝方块 / 边界方块**
（适合单人/局域网合作地图制作）：

- 允许/拒绝方块：管理其上方整列方块的放置与破坏（高处优先）；
- 边界方块：**整列全 Y 阻挡**，垫高跳跃、飞行、挖洞都无法绕过；支持
  「仿基岩版」与「拦截所有实体」两种模式（见上方表格）；
- 白名单 / 方块级白名单 / OP+创造 特权体系；ModMenu + Cloth Config 可视化配置
  （均可选装），主机改配置实时同步给局域网全员。

安装后从创造模式物品栏 **Bedrock Allow / Deny / Border** 分类或使用
`/give @s bedrock_adb:border_block` 获取方块。

**完整中文使用说明见 [`使用说明.md`](使用说明.md)**（含安装要求、界面操作、
两种模式行为矩阵、全部命令、配置文件字段、常见问题与构建指南）。
