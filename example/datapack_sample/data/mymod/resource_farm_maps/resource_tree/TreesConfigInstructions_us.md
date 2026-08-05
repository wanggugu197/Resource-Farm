# Resource Farm: Building content with datapacks

Resource trees, wood base styles, and ore overlay styles are all defined with **datapack JSON** loaded at game startup (every loaded mod jar’s `data/` folder is scanned). The old `config/` register/remove JSON files are **no longer used**.

---

## 1. Overview

| Content | Path pattern | Purpose |
|---------|--------------|---------|
| **Tree definition** | `data/<namespace>/resource_farm_maps/resource_tree/**/*.json` | Register one resource tree |
| **Tree removal** | `data/<namespace>/resource_farm_maps/resource_tree_remove/**/*.json` | Remove a tree after registration |
| **Wood base style** | `data/<namespace>/resource_farm_maps/tree_base_type/<path>.json` | Log/leaves base models + overlays |
| **Ore overlay style** | `data/<namespace>/resource_farm_maps/tree_extra_type/<path>.json` | Ore/crack overlay textures |

- Entry ID = `namespace` + path under the type folder (without `.json`).  
  - e.g. `data/minecraft/resource_farm_maps/tree_base_type/oak.json` → `minecraft:oak`  
  - e.g. `data/mymod/resource_farm_maps/resource_tree/custom/paper.json` → `mymod:custom/paper`  
- In-game tree key is still derived from `item` / `translate_key` (e.g. `minecraft:dirt` → `dirt_tree`).  
- Built-in presets live under: `src/main/resources/data/minecraft/resource_farm_maps/`.  
- Copy-paste samples: [`datapack_sample/`](datapack_sample/).

### Load order

1. Scan styles + tree definitions from all mod jars  
2. Filter by YAML preset `group` switches  
3. Write the tree registry  
4. Apply removals  
5. Register blocks/items  

**Note:** Only JSON packaged inside mod jars is scanned at this stage. World folder datapacks that appear later are **not** applied to tree registration (unlike reloadable recipes).

---

## 2. Register a resource tree

**Path:** `data/<namespace>/resource_farm_maps/resource_tree/<any path>.json`

### Fields

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `item` | string | — | Linked item id (**or** `translate_key`, at least one required) |
| `translate_key` | string | — | Display translation key; also used for id if no item |
| `group` | string | none | Preset group: `base` / `mineral` / `biology` / `agriculture`. Missing/other = **always load** (addons) |
| `automatic_basic_recipe` | bool | `true` | Auto basic product / sapling recipes |
| `product_output` | int | `1` | Basic craft output count |
| `style` | object | required | Visual/growth field group (`tree_style`, `ore_style`, `grower`, `light_level`, `color`); see the table below |
| `fertilize` | object | bone meal default | Fertilizer settings (below) |
| `growth_frequency` | int | `10` | ~1/N chance per random tick |
| `custom_place_block` | string | — | Plantable block id |
| `custom_place_block_tag` | string | — | Plantable block tag |
| `extra_recipes` | object | Extra recipes: extra tree items, custom sapling ingredients, and container recipes; see Example E |

`style` sub-fields:

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `tree_style` | string | required | Base style id (`oak` → `minecraft:oak`) |
| `ore_style` | string | required | Ore overlay style id |
| `grower` | string | required | Grower id; controls which vanilla/custom configured feature is used |
| `light_level` | int | `0` | Glow 0–15 (model `light_emission`) |
| `color` | int or string | `0` | Tint: `0x9E7255`, `#9E7255`, or decimal |

### Example A: Minimal

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

This registers `paper_tree`. Fields not written inside `style` resolve to:

| Field | Runtime value |
|-------|---------------|
| `style.tree_style` | required (example `oak`) |
| `style.ore_style` | required (example `iron`) |
| `style.grower` | required (example `oak`) |
| `style.light_level` | `0` |
| `style.color` | `0` |
| `fertilize` | bone meal, main/secondary chance `0.35` |
| `automatic_basic_recipe` | `true` |
| `product_output` | `1` |
| `growth_frequency` | `10` |

Use this first to confirm loading works, then add visuals, fertilizer, placement rules, and recipes.

### Example B: Full (nether star theme)

```json
{
  "item": "minecraft:nether_star",
  "translate_key": "block.resource_farm.nether_star_tree",
  "automatic_basic_recipe": true,
  "product_output": 1,
  "style": {
    "tree_style": "oak",
    "ore_style": "emerald",
    "grower": "oak",
    "light_level": 12,
    "color": "0xFFFFFF"
  },
  "fertilize": {
    "main_item": "minecraft:blaze_powder",
    "main_chance": 0.3
  },
  "growth_frequency": 100,
  "custom_place_block": "minecraft:beacon"
}
```

### Example C: No item (translate key only)

```json
{
  "group": "base",
  "translate_key": "resource_farm.resource_tree.wood",
  "automatic_basic_recipe": false,
  "style": {
    "tree_style": "dark_oak",
    "ore_style": "nether_quartz",
    "grower": "dark_oak",
    "color": "0xB08F55"
  }
}
```

Provide the translation key in lang files (built-in presets already register several).

### Example D: Custom plantable tag and slower growth

```json
{
  "item": "minecraft:amethyst_shard",
  "translate_key": "block.mymod.amethyst_shard_tree",
  "product_output": 4,
  "style": {
    "tree_style": "mymod:minimal_starwood",
    "ore_style": "mymod:minimal_gold",
    "grower": "oak",
    "light_level": 6,
    "color": "#B985FF"
  },
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

- Use `custom_place_block_tag` when a tree can be planted on a set of blocks; use `custom_place_block` for a single block.
- Higher `growth_frequency` means slower random-tick growth. `40` is roughly one quarter of the default `10` rate.
- `style.tree_style` / `style.ore_style` reference the partial style files in the sample pack; see sections 5 and 6.

### Example E: Extra recipes `extra_recipes`

```json
{
  "item": "minecraft:golden_apple",
  "translate_key": "block.mymod.golden_apple_tree",
  "automatic_basic_recipe": true,
  "product_output": 1,
  "style": {
    "tree_style": "oak",
    "ore_style": "gold",
    "grower": "oak",
    "light_level": 0,
    "color": "0xFFD54F"
  },
  "fertilize": {
    "type": "default"
  },
  "growth_frequency": 10,
  "extra_recipes": {
    "item_outputs": [
      {
        "item": "minecraft:gold_ingot",
        "count": 1
      },
      {
        "item": "minecraft:apple",
        "count": 2
      }
    ],
    "sapling_ingredients": [
      "minecraft:gold_ingot",
      "#minecraft:logs"
    ],
    "container_output": {
      "container": "minecraft:bucket",
      "container_count": 1,
      "output": {
        "item": "minecraft:water_bucket",
        "count": 1
      }
    }
  }
}
```

`extra_recipes` is optional, and each of the three child fields can be omitted independently.

| Field | Type | Description |
|-------|------|-------------|
| `item_outputs` | array | Extra tree item recipes. Each entry is `{ "item": "<item id>", "count": N }` and adds a recipe using this tree's resin and fruit to craft that item, outputting `count` per craft |
| `sapling_ingredients` | string array | Custom sapling recipe inputs. Plain entries are item ids; a leading `#` denotes an **item tag** such as `#minecraft:logs` |
| `container_output` | object | Recipe with an extra input item: `container` is the extra input, `container_count` is how many are required (1–4 only), and `output` is the final output item/count |

- `item_outputs` is independent of `automatic_basic_recipe`: even if basic auto recipes are off, extra item recipes are still generated (subject to the mod's tree item recipe generation toggle).
- `sapling_ingredients` is also independent: a non-empty list always adds a custom sapling recipe (subject to the mod's sapling recipe generation toggle).
- In `container_output`, `container` is a crafting input, not a returned container; for example `bucket` is the input and `water_bucket` is the output.
- `container_count` must be 1–4; out-of-range values fail during loading.

### `group` vs config toggles

Config file: `resource_farm/resource_farm_preset_tree` (Configuration mod)

| `group` | Toggle |
|---------|--------|
| `base` | `presetTreeGeneration.minecraftBase` |
| `mineral` | `minecraftMineral` |
| `biology` | `minecraftBiology` |
| `agriculture` | `minecraftAgriculture` |

- If `enablePresetTreeGroups` is false, those four groups are **all skipped**.  
- Definitions **without** `group` always load (recommended for addon mods).

---

## 3. Fertilize object

### Type shortcuts (checked first; other fields ignored)

```json
{ "type": "default" }
```

→ Bone meal @ 0.35 / 0.35.

```json
{ "type": "null" }
```

→ Not fertilizable.

### Custom items

```json
{
  "main_item": "minecraft:blaze_powder",
  "main_chance": 0.3,
  "secondary_item": "minecraft:bone_meal",
  "secondary_chance": 0.1
}
```

Chances must be in `[0.0, 1.0]`.

---

## 4. Remove trees

**Path:** `data/<namespace>/resource_farm_maps/resource_tree_remove/<any>.json`

Applied **after** all trees are registered. Any of:

```json
"dirt_tree"
```

```json
{ "id": "dirt" }
```

```json
{ "tree_id": "dirt_tree" }
```

- `dirt` or `dirt_tree` both work (`_tree` is appended if missing).  
- Empty object + filename `dirt.json` also resolves to `dirt`.

Id rules:

- `item` `minecraft:dirt` → **`dirt_tree`**  
- `item` `mod:foo_bar` → **`mod_foo_bar_tree`**  
- only `translate_key` `a.b.wood` → **`wood_tree`**

---

## 5. Wood base style `tree_base_type`

**Path:** `data/<namespace>/resource_farm_maps/tree_base_type/<path>.json`  
**Use in trees:** inside `style`, `"tree_style": "oak"` or `"mymod:custom_wood"`

Built-in (`minecraft:`): `oak`, `dark_oak`, `birch`, `spruce`, `jungle`, `acacia`, `cherry`, `mangrove`, `pale_oak`.

Structure (nested):

```json
{
  "type": "oak",
  "models": {
    "sapling_base": "minecraft:block/oak_sapling",
    "leaves_base": "minecraft:block/oak_leaves",
    "log_base": "minecraft:block/oak_log",
    "log_horizontal_base": "minecraft:block/oak_log_horizontal",
    "stripped_log_base": "minecraft:block/stripped_oak_log",
    "stripped_log_horizontal_base": "minecraft:block/stripped_oak_log_horizontal",
    "wood_base": "minecraft:block/oak_wood",
    "stripped_wood_base": "minecraft:block/stripped_oak_wood",
    "planks_base": "minecraft:block/oak_planks"
  },
  "overlays": {
    "sapling_overlay": "resource_farm:block/tree/sapling/oak_sapling_overlay",
    "leaves_overlay": "resource_farm:block/tree/leaves/oak_leaves_overlay",
    "resin": "resource_farm:item/resin/base_resin",
    "resin_overlay": "resource_farm:item/resin/base_resin",
    "fruit": "resource_farm:item/fruit/base_fruit",
    "fruit_overlay": "resource_farm:item/fruit/base_fruit_overlay",
    "clump": "resource_farm:item/crossover/clump",
    "clump_overlay": "resource_farm:item/crossover/clump_overlay"
  },
  "translate_keys": {
    "sapling": "block.resource_farm.tree.sapling",
    "leaves": "block.resource_farm.tree.leaves",
    "log": "block.resource_farm.tree.log",
    "stripped_log": "block.resource_farm.tree.stripped_log",
    "wood": "block.resource_farm.tree.wood",
    "stripped_wood": "block.resource_farm.tree.stripped_wood",
    "planks": "block.resource_farm.tree.planks",
    "resin": "item.resource_farm.tree.resin",
    "fruit": "item.resource_farm.tree.fruit",
    "clump": "item.resource_farm.tree.clump"
  }
}
```

- `models.*` — base block models  
- `overlays.*` — tint / item layer textures  
- Invalid `style.tree_style` falls back to default oak  
- `type`, `models`, `overlays`, `translate_keys`, and their child fields can be omitted. Missing values fall back to oak, built-in Resource Farm textures, and default translation keys.

Copy from: `src/main/resources/data/minecraft/resource_farm_maps/tree_base_type/`.

### Minimal wood base style

```json
{
  "type": "oak",
  "models": {
    "log_base": "minecraft:block/dark_oak_log",
    "log_horizontal_base": "minecraft:block/dark_oak_log_horizontal",
    "planks_base": "minecraft:block/dark_oak_planks"
  },
  "overlays": {
    "leaves_overlay": "resource_farm:block/tree/leaves/dark_oak_leaves_overlay"
  }
}
```

This only changes logs, horizontal logs, planks, and the leaves overlay. Everything else, including sapling models, resin/fruit/clump textures, and translation keys, falls back to the default oak-style values. See [`minimal_starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/minimal_starwood.json).

### Sample file

See [`datapack_sample/data/mymod/resource_farm_maps/tree_base_type/starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/starwood.json):

- File → style id **`mymod:starwood`**
- Reference in a tree `style`: `"tree_style": "mymod:starwood"`
- Demo tree: [`nether_star_themed.json`](datapack_sample/data/mymod/resource_farm_maps/resource_tree/nether_star_themed.json) (also uses custom `mymod:star_ore`)

Easiest workflow: copy built-in `oak.json` / `dark_oak.json`, then only change `models` paths and `type`.

---

## 6. Ore overlay style `tree_extra_type`

**Path:** `data/<namespace>/resource_farm_maps/tree_extra_type/<path>.json`  
**Use:** inside `style`, `"ore_style": "iron"` or `"mymod:my_ore"`

```json
{
  "base": "resource_farm:block/ore/iron",
  "center": "resource_farm:block/ore/center"
}
```

| Field | Role |
|-------|------|
| `base` | Main overlay texture; defaults to `resource_farm:block/ore/iron` |
| `center` | Center layer (e.g. log face); defaults to `resource_farm:block/ore/center` |

Minimal example:

```json
{
  "base": "resource_farm:block/ore/gold"
}
```

The center layer then falls back to `resource_farm:block/ore/center`. See [`minimal_gold.json`](datapack_sample/data/mymod/resource_farm_maps/tree_extra_type/minimal_gold.json).

Built-in: `copper`, `diamond`, `emerald`, `gold`, `iron`, `lapis`, `nether_gold`, `nether_quartz`, `redstone`, `crack`.

---

## 7. For addon / datapack authors

1. Layout:

```text
src/main/resources/data/<yourmodid>/resource_farm_maps/
  resource_tree/
    my_item.json
  tree_base_type/          # optional
  tree_extra_type/         # optional
  resource_tree_remove/    # optional
```

2. Prefer **no** `group` (or a custom group name) so player preset toggles do not skip your trees.  
3. Reuse built-in styles inside `style`: `"tree_style": "oak"`, `"ore_style": "diamond"`.  
4. For new looks, add style JSON first, then reference it inside `style`: `"tree_style": "mymod:my_wood"`.  
5. Ensure `data/` is packaged into the jar. Check logs:

```text
[ResourceFarm] resource_farm_maps: ... base, ... extra, ... grower, ... tree json, ... remove
[ResourceFarm] Resource tree registration: applied N tree(s) (skipped M by preset), removed R.
```

---

## 8. Migration from old config JSON

| Old (removed) | New (datapack) |
|---------------|----------------|
| `config/.../resource_tree_register_configs.json` | `data/.../resource_farm_maps/resource_tree/*.json` |
| `resource_tree_remove_configs.json` | `resource_tree_remove/*.json` |
| camelCase `treeStyle` / `colors` | snake_case `tree_style` / `color` |
| `fertilizeSetting` | `fertilize` (supports `type`) |
| Flat `tree_style` / `ore_style` / `grower` / `light_level` / `color` | Grouped under `style` (e.g. `style.tree_style`) |
| One JSON array | **One file per tree** |

Old config paths are **not** read anymore.

---

## 9. Sample index

| Location | Notes |
|----------|--------|
| [datapack_sample/](datapack_sample/) | Starter layout (includes `tree_base_type` + `tree_extra_type`) |
| Wood base sample | [`tree_base_type/starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/starwood.json) |
| Partial wood base sample | [`tree_base_type/minimal_starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/minimal_starwood.json) |
| Ore overlay sample | [`tree_extra_type/star_ore.json`](datapack_sample/data/mymod/resource_farm_maps/tree_extra_type/star_ore.json) |
| Minimal ore overlay sample | [`tree_extra_type/minimal_gold.json`](datapack_sample/data/mymod/resource_farm_maps/tree_extra_type/minimal_gold.json) |
| Custom plantable tag sample | [`resource_tree/custom_soil_and_tag.json`](datapack_sample/data/mymod/resource_farm_maps/resource_tree/custom_soil_and_tag.json) |
| Extra recipes sample | [`resource_tree/extra_recipes_demo.json`](datapack_sample/data/mymod/resource_farm_maps/resource_tree/extra_recipes_demo.json) |
| Built-in presets | `src/main/resources/data/minecraft/resource_farm_maps/` |

中文版：[TreesConfigInstructions_cn.md](TreesConfigInstructions_cn.md)
