<h1 align="center">Resource Farm</h1>

<p align="center">
  <strong>Plant a resource. Grow an empire.</strong>
</p>

<p align="center">
  <a href="https://www.curseforge.com/minecraft/mc-mods/resource-farm"><img src="https://img.shields.io/badge/Available%20for-MC%2026.1.2%20NeoForge-informational?style=for-the-badge" alt="Supported Versions"></a>
  <a href="https://github.com/wanggugu197/Resource-Farm/blob/main/LICENSE"><img src="https://img.shields.io/badge/LICENSE-LGPL--v3.0-orange?style=for-the-badge" alt="License"></a>
  <br>
  <a href="https://www.curseforge.com/minecraft/mc-mods/resource-farm"><img src="https://cf.way2muchnoise.eu/1422330.svg?badge_style=for_the_badge" alt="CurseForge"></a>
  <a href="https://github.com/wanggugu197/Resource-Farm"><img src="https://img.shields.io/badge/GitHub-Resource--Farm-181717?style=for-the-badge&logo=github" alt="GitHub"></a>
</p>

---

## Welcome to the Farm

What if diamonds didn’t hide under mountains—but **ripened on branches**?  
What if blaze rods, sculk, honey, and even nether stars could be **cultivated** like apples in an orchard?

**Resource Farm** turns Minecraft’s grind into a living garden. Every resource tree is a complete little ecosystem: saplings that grow on their own terms, leaves that drip resin and fruit, logs that look like ore veins painted into wood, and recipes that turn harvests back into the materials you need. Plant once, prune often, and watch rare goods grow on your schedule—not the world’s random luck.

Whether you want a peaceful mid-game factory of renewable materials, a pack-maker’s balance dial, or a decorative forest glowing with custom colors, this mod is built so **your farm feels like your rules**.

> **QQ community:** `978108198`

---

## At a Glance

| | |
|---|---|
| **Loader** | NeoForge |
| **Minecraft** | 26.1.2+ |
| **Mod ID** | `resource_farm` |
| **Author** | maple197 |
| **License** | LGPL-3.0 |
| **Repo / issues** | [GitHub](https://github.com/wanggugu197/Resource-Farm) · [Issues](https://github.com/wanggugu197/Resource-Farm/issues) |

---

## Core Fantasy: Resource Trees

Each resource tree is **not just a recolored oak**. It is a full set of blocks and items that form a closed farming loop:

### Blocks

| Block | Role |
|-------|------|
| **Sapling** | Start of the cycle. Random-tick growth, optional custom soil/placement rules, and tooltips for fertilizer & placeable surfaces. |
| **Leaves** | Break for a chance at **resin**, **fruit**, and more saplings (fortune-friendly leaves-style loot). |
| **Log** | Main trunk; looks like a wood base with a resource “ore” overlay and your configured tint. |
| **Stripped log / wood / stripped wood / planks** | Optional wood pipeline (toggleable in config). Same visual language as vanilla woodcraft. |

### Items

| Item | Role |
|------|------|
| **Resin** | Smoked/smelted from logs (and planks); used to craft the tree’s main product and more. |
| **Fruit** | Dropped from leaves; pairs with resin in product recipes. |
| **Clump** | Optional “fragment” item (crossover-style dual-layer look), enabled by default via config. |

### Look & feel

- **Tree style** — which vanilla silhouette to use (oak, birch, spruce, jungle, acacia, dark oak, cherry, mangrove, pale oak…).
- **Ore style** — overlay texture language (iron, gold, diamond, redstone, lapis, nether quartz…).
- **Color** — a single accent tint baked into models for leaves, saplings, resin, fruit, clump, and overlays.
- **Light emission** — optional self-glow on tint overlay models (`0–15`), for ethereal forests that shine in the dark *(visual glow; not necessarily world light)*.

Creative tabs: **Resource Farm** and **Resource Farm \| Resource Tree**.

---

## How You Play

### 1. Craft a sapling

For a simple product tree, the usual path is:

1. Obtain materials related to that resource (or use JEI/EMI to look up the sapling).
2. Craft the **resource sapling**—often a ring of the resource around a vanilla sapling tag item, or a special multi-ingredient pattern for themed trees.
3. Check the sapling tooltip for **fertilizer** and **where it can be planted**.

### 2. Plant & grow

- Place the sapling on valid ground (dirt-like by default, or a **custom block / block tag** if configured).
- Trees grow on **random ticks**; `growthFrequency` controls how rare growth is (higher = slower).
- **Fertilizer**: by default bone meal-style items with a success chance; special trees may demand blaze powder, chorus fruit, etc.

### 3. Harvest the orchard

- **Leaves** → resin, fruit, saplings (and leaf blocks themselves).
- **Logs / wood** → smoke or smelt into **resin**; craft into **planks** and wood forms like vanilla.
- **Resin + fruit** → craft the tree’s **main product** (e.g. raw iron, diamonds, honeycombs…), with `productOutput` controlling batch size.

### 4. Expand the farm

Once you have resin and fruit, you can rebuild products and more saplings. Rare trees (nether star, dragon egg…) grow slowly and may use special soils or fertilizers—perfect end-game “prestige orchards.”

---

## Built-in Preset Forests

Out of the box, **preset tree groups** can be toggled independently. Disable what you don’t want for pack balance.

### Minecraft Base

Everyday terrain & utility: dirt, clay, gravel, sand, stone, deepslate, obsidian, netherrack, soul sand, end stone, ice, snowballs, glowstone dust, sculk, experience bottles, plus themed trees for **wood**, **dyes**, **lava**, and **water** (bucket workflows).

### Minecraft Mineral

The classic mining ladder—without mining: flint, coal, raw copper/iron/gold, lapis, redstone, diamond, emerald, amethyst, quartz, ancient debris (and a few block-tier bonus recipes).

### Minecraft Biology

Living loot, farmed: breeding products, aquatic drops, bees/honey, rabbits, armadillo & turtle scutes, “minor creature” drops, ender pearls, slime, phantom membrane, breeze rods, ghast tears, blaze rods, wither skeleton skulls, shulker shells…

### Minecraft Agriculture

Crops and wonders: cultivation staples, berries, kelp, nether wart, prismarine, chorus, echo shards, and slow-grow legends like **nether star** (beacon soil, blaze powder boost) and **dragon egg**.

> All of these can be switched off in  
> `config/resource_farm/resource_farm_preset_tree.yaml`.

---

## Configuration for Players & Pack Makers

Configs live under `config/resource_farm/`.

### Main config — `resource_farm.yaml`

Developer helpers:

| Option | Default | Meaning |
|--------|---------|---------|
| `dev.dumpData` | `false` | Dump registered data for debugging |
| `dev.dumpAssets` | `false` | Dump generated assets for debugging |

### Preset & generation — `resource_farm_preset_tree.yaml`

**Block generation**

- Stripped logs, wood, stripped wood, planks — on/off  
- Clump: `autoGenerateClump` / `forceGenerateClump`

**Recipe generation**

- Tree product crafts  
- Sapling crafts  
- Resin smelting / smoking  
- Basic wood conversion (log → planks / wood)

**Preset groups**

- Master switch: `enablePresetTreeGroups`  
- Per group: `minecraftBase`, `minecraftMineral`, `minecraftBiology`, `minecraftAgriculture`

### Custom trees via JSON

You can **register** and **remove** trees without writing Java:

| File | Purpose |
|------|---------|
| `config/resource_farm/resource_tree_register_configs.json` | Add trees at startup |
| `config/resource_farm/resource_tree_remove_configs.json` | Strip trees you don’t want |

Examples and field-by-field docs:

- [TreesConfigInstructions (English)](./example/TreesConfigInstructions_us.md)
- [TreesConfigInstructions (中文)](./example/TreesConfigInstructions_cn.md)
- Sample JSON: [`example/resource_tree_register_configs.json`](./example/resource_tree_register_configs.json)

#### Quick field reference

| Field | Role | Default-ish |
|-------|------|-------------|
| `item` **or** `translateKey` | Identity of the tree (one required) | — |
| `automaticBasicRecipe` | Auto resin+fruit → product & sapling recipes | `true` |
| `productOutput` | How many products per craft | `1` |
| `treeStyle` | Wood silhouette (`oak`, `birch`, …) | `oak` |
| `oreStyle` | Overlay style (`iron`, `diamond`, …) | `iron` |
| `fertilizeSetting` | Main/secondary fertilizer items & chances | Bone meal ~35% |
| `growthFrequency` | 1/N chance per random tick | `10` |
| `customPlaceBlock` / `customPlaceBlockTag` | Where the sapling may be planted | Vanilla-like |
| `lightLevel` | Model self-glow `0–15` | `0` |
| `colors` | ARGB/hex tint (e.g. `"0xFF99FFFF"`) | `0` |

Minimal tree:

```json
[
  { "item": "minecraft:paper" }
]
```

Themed tree (sketch):

```json
[
  {
    "item": "minecraft:amethyst_shard",
    "treeStyle": "dark_oak",
    "oreStyle": "emerald",
    "productOutput": 16,
    "lightLevel": 7,
    "colors": "0xFF9966CC",
    "customPlaceBlockTag": "minecraft:stone_ore_replaceables"
  }
]
```

---

## Recipes in Practice

(Exact patterns are in-game; JEI/EMI recommended.)

| Flow | Typical result |
|------|----------------|
| Logs / wood → **smoking** | Resin (solid yield) |
| Planks → **smelting / smoking** | Resin (smaller yield) |
| Resin + fruit (shaped) | Main **product** × `productOutput` |
| Product + saplings tag | More **resource saplings** |
| Logs → planks / wood | Vanilla-like wood pipeline |
| Special presets | Extra outputs (e.g. charcoal from coal tree, honey bottles from honeycomb tree, multi-log “wood” tree) |

Leaves use a **vanilla leaves-style loot table**: self-drops, saplings, and weighted resin/fruit rolls (fortune applies where relevant). Logs and wood forms drop themselves.

---

## For Developers

Secondary notes for mod/pack authors who dig into the codebase.

### Architecture (high level)

| Area | Responsibility |
|------|----------------|
| `TreeRegister` / `TreeBuilder` | Dynamic registration of blocks & items per tree config |
| `TreeModelRenderer` | Runtime dynamic assets: multipart block models, tinted overlays, item definitions, element `light_emission` |
| `TreeRecipe` / `TreesCommonRegister` | Common + special recipes for presets |
| `TreeLootInsert` | Dynamic loot table injection |
| `TreeTagInsert` | Tags for leaves, logs, saplings, resin, fruit, clump… |
| `TreeRegisterConfig` / `TreeRemoveConfig` | JSON-driven add/remove |
| `ResourceTreeTypes` / `ResourceOreTypes` | Style catalogs for wood base + ore overlay |

### Extending styles

- Add entries in `ResourceTreeTypes` / `ResourceOreTypes` (textures under `assets/resource_farm/…`).
- Register trees in Java via `TreeRegister` helpers, or ship JSON configs with your pack.

### Light emission note (26.1+)

Block model **self-glow** must be set on the **element** as `light_emission` (0–15), not on individual faces. This mod’s generator follows that vanilla contract (see `cross_emissive` / firefly bush patterns).

### Build

```bash
./gradlew build
```

Requirements track **Minecraft 26.1.2** and **NeoForge 26.1.x** (see `gradle.properties`). Local helper library jars may be required under `libs/` depending on your setup.

### Dependencies (runtime concept)

- NeoForge  
- Configuration library used for YAML configs  
- MapleUtilLib / registry helpers as bundled by the project  

(Check the published CurseForge page for exact required dependencies for players.)

---

## Design Goals

1. **Readable gameplay** — sapling tooltips, creative tabs, clear resin/fruit/product loop.  
2. **Pack-first flexibility** — toggle presets, strip trees, add JSON trees, tune recipes & blocks.  
3. **Visual identity** — tree style × ore style × color × optional glow.  
4. **Data-driven where it counts** — registration, models, loot, recipes, and tags generated from config rather than hundreds of hand-written JSON files.

---

## Links

- **CurseForge:** [Resource Farm](https://www.curseforge.com/minecraft/mc-mods/resource-farm)  
- **Source:** [wanggugu197/Resource-Farm](https://github.com/wanggugu197/Resource-Farm)  
- **Issues:** [GitHub Issues](https://github.com/wanggugu197/Resource-Farm/issues)  
- **Config guide (EN):** [example/TreesConfigInstructions_us.md](./example/TreesConfigInstructions_us.md)  
- **Config guide (ZH):** [example/TreesConfigInstructions_cn.md](./example/TreesConfigInstructions_cn.md)  

---

<p align="center">
  <em>Stop hunting every last ore vein.<br>Start planting them.</em>
</p>
