# Credits

**Bedrock Allow Deny Border** (`bedrock_adb`)

Authored and maintained by **zihan_ds** (`bedrock-adb`).

## Project history

- The mod was originally developed and published (up to version **1.1.3**) as a
  jar-only Fabric mod by the same author, targeting Minecraft 1.21.
- This repository contains the **reconstructed source tree** of that codebase
  (decompiled and cleaned up from the 1.1.3 jar, remapped to Yarn names, with a
  small number of decompiler-artifact fixes), plus the continued development:
  - **1.2.0** — border block modes + Mod Menu / Cloth Config GUI + LAN config
    sync of the new option;
  - **1.2.1+1.21** — whole-column (all-Y) border barrier with projectile
    interception.
- If you spot any code that looks like it came from another MIT-licensed
  project without attribution, please open an issue so we can credit it.

## Design / behavioural reference

- Block semantics follow the Bedrock / Education Edition **Allow**, **Deny**
  and **Border** blocks (see [minecraft.wiki](https://minecraft.wiki/)).
- Border behaviour matrices, privilege model and FAQ are documented in
  [`使用说明.md`](使用说明.md).

## Third-party libraries used

- [Fabric API](https://github.com/FabricMC/fabric-api) (MIT) — events,
  networking, item groups, chunk events.
- [Fabric Loader](https://github.com/FabricMC/fabric-loader) (Apache-2.0) and
  [Yarn](https://github.com/FabricMC/yarn) mappings (MIT).
- [Mod Menu](https://github.com/TerraformersMC/ModMenu) (MIT) — config button
  entry point (optional at runtime).
- [Cloth Config](https://github.com/shedaniel/cloth-config) (LGPL-3.0) —
  config screen rendering (optional at runtime).
