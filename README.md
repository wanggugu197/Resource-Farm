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
In datapack JSON these visuals are grouped under the `style` object (`tree_style`, `ore_style`, `grower`, `light_level`, `color`).

---

## How You Play

### 1. Craft a sapling

For a simple product tree, the usual path is:

1. Obtain materials related to that resource (or use JEI/EMI to look up the sapling).
2. Craft the **resource sapling**—often a ring of the resource around a vanilla sapling tag item, or a special multi-ingredient pattern for themed trees.
3. Check the sapling tooltip for **fertilizer** and **where it can be planted**.

### 2. Plant & grow

- Place the sapling on valid ground (dirt-like by default, or a **custom block / block tag** if configured).
- Trees grow on **random ticks**; datapack field `growth_frequency` controls how rare growth is (higher = slower).
- **Fertilizer**: by default bone meal-style items with a success chance; special trees may demand blaze powder, chorus fruit, etc.

### 3. Harvest the orchard

- **Leaves** → resin, fruit, saplings (and leaf blocks themselves).
- **Logs / wood** → smoke or smelt into **resin**; craft into **planks** and wood forms like vanilla.
- **Resin + fruit** → craft the tree’s **main product** (e.g. raw iron, diamonds, honeycombs…), with `product_output` controlling batch size.

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
> `config/resource_farm/resource_farm_preset_tree` (extension depends on the Configuration library output).

---

## Configuration for Players & Pack Makers

Configs live under `config/resource_farm/`.

### Main config — `resource_farm`

Developer helpers:

| Option | Default | Meaning |
|--------|---------|---------|
| `dev.dumpData` | `false` | Dump registered data for debugging |
| `dev.dumpAssets` | `false` | Dump generated assets for debugging |

### Preset & generation — `resource_farm_preset_tree`

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

### Custom trees via datapacks

Trees, wood styles, ore overlays, and optional grower presets are built from **datapack JSON** inside mod jars (`data/`), scanned at startup—no Java required for addons. These files are part of startup registration, so package them in a mod jar or built-in resources; late world datapacks are not applied to tree registration.

| Path under `data/<namespace>/resource_farm_maps/` | Purpose |
|---------------------------------------------------|---------|
| `resource_tree/**/*.json` | Register one tree per file |
| `resource_tree_remove/**/*.json` | Remove a tree after registration |
| `tree_base_type/<id>.json` | Wood / leaves base models + overlays |
| `tree_extra_type/<id>.json` | Ore overlay textures |
| `resource_tree_grower/<id>.json` | Optional shared grower definitions |

Full field docs and migration notes:

- [Datapack guide (English)](example/datapack_sample/data/mymod/resource_farm_maps/resource_tree/TreesConfigInstructions_us.md)
- [数据包说明（中文）](example/datapack_sample/data/mymod/resource_farm_maps/resource_tree/TreesConfigInstructions_cn.md)
- Copy-paste sample: [`example/datapack_sample/`](example/datapack_sample/)

#### Quick tree definition

`data/mymod/resource_farm_maps/resource_tree/paper.json`:

```json
{
  "item": "minecraft:paper",
  "style": {
    "tree_style": "oak",
    "ore_style": "iron",
    "grower": "oak",
    "color": "0xF5F5DC"
  }
}
```

This minimal file still registers `paper_tree`: the `style` group requires `tree_style` / `ore_style` / `grower`; omitted `light_level` and `color` fall back to `0`, plus default bone meal fertilizer, `product_output: 1`, and `growth_frequency: 10`.

More explicit item-backed tree:

```json
{
  "item": "minecraft:paper",
  "style": {
    "tree_style": "birch",
    "ore_style": "iron",
    "grower": "birch",
    "light_level": 0,
    "color": "0xF5F5DC"
  },
  "product_output": 16,
  "fertilize": {
    "type": "default"
  }
}
```

Themed tree with custom style ids and planting rules:

```json
{
  "item": "minecraft:amethyst_shard",
  "translate_key": "block.mymod.amethyst_shard_tree",
  "style": {
    "tree_style": "mymod:minimal_starwood",
    "ore_style": "mymod:minimal_gold",
    "grower": "oak",
    "light_level": 6,
    "color": "#B985FF"
  },
  "product_output": 4,
  "fertilize": {
    "main_item": "minecraft:glowstone_dust",
    "main_chance": 0.2,
    "secondary_item": "minecraft:bone_meal",
    "secondary_chance": 0.05
  },
  "growth_frequency": 40,
  "custom_place_block_tag": "minecraft:crystal_sound_blocks"
}
```

Style JSON can also be partial. Missing `tree_base_type` fields fall back to oak models, built-in Resource Farm overlays, and default translation keys. Missing `tree_extra_type.base` / `center` fall back to the iron overlay and Resource Farm center texture. Preset groups (`group`: `base` / `mineral` / `biology` / `agriculture`) can be toggled in config; definitions **without** `group` always load (recommended for addons).

---

## Recipes in Practice

(Exact patterns are in-game; JEI/EMI recommended.)

| Flow | Typical result |
|------|----------------|
| Logs / wood → **smoking** | Resin (solid yield) |
| Planks → **smelting / smoking** | Resin (smaller yield) |
| Resin + fruit (shaped) | Main **product** × `product_output` |
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
| `ResourceFarmMaps` | Startup scan and decode of `resource_farm_maps` datapack JSON |
| `ResourceTreeConfig` / `ResourceTreeBaseType` / `ResourceTreeExtraType` / `ResourceTreeGrower` | Runtime data records and codecs for trees, styles, overlays, and grower presets |
| `TreeInitialization` / `TreeBuilder` | Dynamic registration of blocks & items per tree config |
| `TreeModelRenderer` | Runtime dynamic assets: multipart block models, tinted overlays, item definitions, element `light_emission` |
| `TreeRecipe` / `TreesCommonRegister` | Common + special recipes for presets |
| `TreeLootInsert` | Dynamic loot table injection |
| `TreeTagInsert` | Tags for leaves, logs, saplings, resin, fruit, clump… |

### Extending with JSON

- Add `resource_tree/*.json` for new trees.
- Add `tree_base_type/*.json` / `tree_extra_type/*.json` for reusable visual styles.
- Add `resource_tree_grower/*.json` only when built-in grower ids (`oak`, `birch`, `spruce`, `jungle`, `acacia`, `dark_oak`, `cherry`, `mangrove`) are not enough.
- Keep addon trees without `group` unless you deliberately want Resource Farm preset toggles to skip them.

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
4. **Data-driven where it counts** — registration, models, loot, recipes, and tags generated from `resource_farm_maps` datapack JSON rather than hundreds of hand-written files.

---

## Links

- **CurseForge:** [Resource Farm](https://www.curseforge.com/minecraft/mc-mods/resource-farm)  
- **Source:** [wanggugu197/Resource-Farm](https://github.com/wanggugu197/Resource-Farm)  
- **Issues:** [GitHub Issues](https://github.com/wanggugu197/Resource-Farm/issues)  
- **Datapack guide (EN):** [example/TreesConfigInstructions_us.md](https://github.com/wanggugu197/Resource-Farm/blob/26.1.2/example/datapack_sample/data/mymod/resource_farm_maps/resource_tree/TreesConfigInstructions_us.md)
- **数据包说明（中文）:** [example/TreesConfigInstructions_cn.md](https://github.com/wanggugu197/Resource-Farm/blob/26.1.2/example/datapack_sample/data/mymod/resource_farm_maps/resource_tree/TreesConfigInstructions_cn.md)

---

<p align="center">
  <em>Stop hunting every last ore vein.<br>Start planting them.</em>
</p>
