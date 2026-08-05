# Datapack sample (`mymod`)

Copy the `data/` folder into **your mod jar resources** (or any mod that depends on Resource Farm):

```text
data/mymod/resource_farm_maps/
  resource_tree/              # add trees (no group = always load)
    paper.json                # minimal — reuses built-in birch + iron
    nether_star_themed.json   # uses custom starwood + star_ore
    custom_soil_and_tag.json  # custom soil tag + custom fertilizer
    extra_recipes_demo.json   # item_outputs / sapling_ingredients / container_output
    translate_only_demo.json
  resource_tree_remove/
    optional_remove_dirt.json
  tree_base_type/
    starwood.json             # custom wood base style → id mymod:starwood
    minimal_starwood.json     # partial style; omitted fields fall back to oak defaults
  tree_extra_type/
    star_ore.json             # custom ore overlay → id mymod:star_ore
    minimal_gold.json         # only sets base; center falls back to Resource Farm center
```

### Style IDs

| File | Entry id | Use in tree JSON |
|------|----------|------------------|
| `tree_base_type/starwood.json` | `mymod:starwood` | `"style": { "tree_style": "mymod:starwood" }` |
| `tree_base_type/minimal_starwood.json` | `mymod:minimal_starwood` | `"style": { "tree_style": "mymod:minimal_starwood" }` |
| `tree_extra_type/star_ore.json` | `mymod:star_ore` | `"style": { "ore_style": "mymod:star_ore" }` |
| `tree_extra_type/minimal_gold.json` | `mymod:minimal_gold` | `"style": { "ore_style": "mymod:minimal_gold" }` |

`starwood` is based on dark oak models + Resource Farm overlays (copy built-in
`tree_base_type/oak.json` and swap model paths for a real custom look).

Namespace `mymod` is only an example — use your real mod id.

### What each tree sample demonstrates

| File | Demonstrates | Resulting tree key |
|------|--------------|--------------------|
| `resource_tree/paper.json` | A normal item-backed tree with an explicit `style` group (tree/ore style, grower, glow, tint) and fertilizer. | `paper_tree` |
| `resource_tree/nether_star_themed.json` | A full themed tree using custom wood/ore styles, a special plantable block, custom fertilizer, and light emission. | `nether_star_tree` |
| `resource_tree/custom_soil_and_tag.json` | A tree that can be planted on a block tag, uses the new partial style examples, and keeps recipe generation enabled by default. | `amethyst_shard_tree` |
| `resource_tree/extra_recipes_demo.json` | All three `extra_recipes` groups: extra item outputs, custom sapling ingredients (item + item tag), and a container recipe. | `golden_apple_tree` |
| `resource_tree/translate_only_demo.json` | A tree without a linked item. `translate_key` is used for display and for deriving the id. | `demo_tree` |

### Defaults and fallback examples

Tree files require `item` or `translate_key`, plus the `style` group with `tree_style` / `ore_style` / `grower`. Other fields use runtime defaults:

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

This still registers `paper_tree`, uses `style.tree_style: oak`, `style.ore_style: iron`, `style.grower: oak`, `style.light_level: 0`, default bone meal fertilizer, `product_output: 1`, and `growth_frequency: 10`. Visual/glow/tint fields live inside `style`; see the config docs for the full field table.

Style files can also be partial. `tree_base_type/minimal_starwood.json` intentionally only overrides a few model/overlay fields; missing `sapling_base`, `leaves_base`, item overlays, and translation keys fall back to Resource Farm's oak defaults. `tree_extra_type/minimal_gold.json` only sets `base`; missing `center` falls back to `resource_farm:block/ore/center`.

### Extra recipes

`resource_tree/extra_recipes_demo.json` demonstrates the new `extra_recipes` block:

- `item_outputs` adds extra tree item recipes; each entry is `{ "item": "...", "count": N }`.
- `sapling_ingredients` adds a custom sapling recipe; entries can be item ids or `#tag` item tags such as `#minecraft:logs`.
- `container_output` adds a recipe with `container` as an extra crafting input (`container_count` 1–4) and `output` as the final item/count.

`extra_recipes` and all of its child fields are optional. The three child fields can also be combined freely. See the config docs for the full field table and limits.

### Notes for real addons

- Keep `group` absent unless you deliberately want Resource Farm preset toggles to disable your tree.
- Use your actual namespace instead of `mymod`; the namespace becomes part of style ids such as `yourmod:minimal_gold`.
- Put translations such as `block.mymod.amethyst_shard_tree` in your normal `assets/<modid>/lang/*.json`.
- `tree_extra_type` currently uses `base` and `center`; extra keys are ignored by the loader.

Docs:

- [中文说明](data/mymod/resource_farm_maps/resource_tree/TreesConfigInstructions_cn.md)
- [English](data/mymod/resource_farm_maps/resource_tree/TreesConfigInstructions_us.md)
