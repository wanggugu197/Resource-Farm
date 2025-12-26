| Core Fields         | Function                                                                                                             | Value Examples                         | Default Value                |
|---------------------|----------------------------------------------------------------------------------------------------------------------|----------------------------------------|------------------------------|
| item                | Item ID associated with the resource tree (alternative to translateKey; translateKey takes priority if both are set) | "minecraft:nether_star"                | null                         |
| translateKey        | Translation key (alternative to item; used for in-game localization and ID generation if item is null)               | "block.resource_farm.nether_star_tree" | null                         |
| treeStyle           | Tree style (corresponds to ResourceTreeTypes enum)                                                                   | "oak"/"spruce"/"birch"                 | "oak"                        |
| oreStyle            | Ore style (corresponds to ResourceOreTypes enum)                                                                     | "iron"/"diamond"/"netherite"           | "iron"                       |
| fertilizeSetting    | Fertilization configuration (nested object, refer to FertilizeSettings class)                                        | See examples                           | Default fertilization config |
| growthFrequency     | Growth frequency (1/x chance of growing per random tick)                                                             | 5/10                                   | 10                           |
| customPlaceBlock    | Custom placement block ID                                                                                            | "minecraft:netherrack"                 | null                         |
| customPlaceBlockTag | Custom placement block tag                                                                                           | "minecraft:nether_blocks"              | null                         |
| lightLevel          | Resource tree light level (0-15, Minecraft's light level range)                                                      | 10/15                                  | 0                            |
| colors              | Resource tree color (hexadecimal integer, e.g., 0xFF0000 = red)                                                      | 0xFF5500/0x00FFFF                      | 0                            |

Example 1: Full Configuration (Nether Star) – All Fields Customized
json
{
  // Core field: item (used for ID generation by priority)
  "item": "minecraft:nether_star",
  // Translation key (fallback for ID generation and in-game localization)
  "translateKey": "block.resource_farm.nether_star_tree",
  // Tree style: customized to crimson forest style (falls back to oak if enum value doesn't exist)
  "treeStyle": "crimson",
  // Ore style: customized for Nether Star (falls back to iron if enum value doesn't exist)
  "oreStyle": "nether_star",
  // Fertilization configuration: custom primary/secondary items + success rates
  "fertilizeSetting": {
    "mainItem": "minecraft:glow_berries",    // Primary fertilization item: Glow Berries
    "mainChance": 0.5,                       // Primary success rate: 50%
    "secondaryItem": "minecraft:nether_wart",// Secondary fertilization item: Nether Wart
    "secondaryChance": 0.2                   // Secondary success rate: 20%
  },
  // Growth frequency: 1/100 chance of growing per random tick
  "growthFrequency": 100,
  // Custom placement block: Netherrack
  "customPlaceBlock": "minecraft:netherrack",
  // Custom placement block tag: Nether blocks tag (example)
  "customPlaceBlockTag": "minecraft:nether_blocks",
  // Light level: 10 (medium brightness)
  "lightLevel": 10,
  // Color: Magenta (0xFF800080)
  "colors": "0xFF800080"
}
Note: Demonstrates a full custom configuration with no default value fallbacks, ideal for resource trees requiring precise control.

Example 2: Partial Fields Missing (Heart of the Sea) – Relying on Defaults
json
{
  "item": "minecraft:heart_of_the_sea",
  "translateKey": "block.resource_farm.heart_of_the_sea_tree",
  // Missing treeStyle → defaults to oak
  // Missing oreStyle → defaults to iron
  // Missing fertilizeSetting → defaults to Bone Meal (35% success rate)
  // Missing customPlaceBlock → null
  // Missing customPlaceBlockTag → null
  "lightLevel": 8,  // Only customize light level
  "colors": "0xFF0000FF"  // Only customize color to blue (ocean theme)
}
Note: Demonstrates a scenario where required fields (item/translateKey) are filled, and missing fields use defaults to simplify configuration.

Example 3: Custom Tag + Enum (Amethyst) – Tag Instead of Block
json
{
  "item": "minecraft:amethyst_shard",
  "treeStyle": "azalea",  // Azalea tree style (if enum exists)
  "oreStyle": "amethyst", // Amethyst ore style
  // Omit customPlaceBlock; use tag to specify placement blocks
  "customPlaceBlockTag": "minecraft:stone_ore_replaceables",
  "lightLevel": 7,
  "colors": "0xFF9966CC"  // Amethyst color
}
Note: Demonstrates using customPlaceBlockTag instead of customPlaceBlock for bulk placement block specification (e.g., all replaceable stones).

Example 4: Invalid Value Scenario (Smithing Template) – Fallback Behavior
json
{
  "item": "minecraft:smithing_template",
  "treeStyle": "invalid_tree_style",  // Invalid tree style → falls back to oak
  "oreStyle": "invalid_ore_style",    // Invalid ore style → falls back to iron
  "customPlaceBlock": "minecraft:sculk_shrieker", // Sculk Shrieker (valid block)
  "lightLevel": 20,  // Exceeds light level range (0-15) → passed as-is (game auto-clamps)
  "colors": "-1"     // Negative color value → passed as-is (handled by ColoringSettings)
}
Note: Intentionally uses invalid values to demonstrate fallback logic: invalid enums → defaults, invalid block IDs → null, value out of range → passed as-is.

Example 5: TranslateKey Only (Ender Pearl) – No Item
json
{
  // No item; use translateKey only (for ID generation)
  "translateKey": "block.resource_farm.ender_pearl_tree",
  "treeStyle": "dark_oak",
  "oreStyle": "ender",
  "fertilizeSetting": {
    "type": "default"  // Use default fertilization configuration directly
  },
  "customPlaceBlock": "minecraft:end_stone",
  "lightLevel": 15,  // Maximum brightness
  "colors": "0xFF000000"  // Black (Ender theme)
}
Note: Demonstrates a scenario where item is null and translateKey is used alone. The code generates an ID by extracting the part after the last dot in the translateKey.

Example 6: Item Only (Paper) – Minimal Core Configuration
json
{
  // Only fill the item field; all others use defaults
  "item": "minecraft:paper"
  // Missing translateKey → generate ID from item (paper)
  // Missing treeStyle → oak
  // Missing oreStyle → iron
  // Missing fertilizeSetting → default Bone Meal configuration
  // Missing customPlaceBlock/customPlaceBlockTag → null
  // Missing lightLevel → 0
  // Missing colors → 0
}
Note: Most minimal configuration – only the required item field is filled, and all other fields rely on defaults. Ideal for quickly registering basic resource trees.

Example 7: Custom Fertilization Configuration (Totem of Undying) – Special Rules
json
{
  "item": "minecraft:totem_of_undying",
  "translateKey": "block.resource_farm.totem_tree",
  // Custom fertilization: primary item only, no secondary item
  "fertilizeSetting": {
    "mainItem": "minecraft:gold_ingot",  // Primary fertilization item: Gold Ingot
    "mainChance": 0.8                    // Success rate: 80%
  },
  "customPlaceBlock": "minecraft:gold_blocks", // Invalid block ID → parsed as null
  "lightLevel": 9,
  "colors": "0xFFFFD700"  // Gold color (Totem of Undying theme)
}
Note: Demonstrates a configuration with only a primary fertilization item. If secondaryItem is omitted, FertilizeSettings automatically sets it to null with 0% success rate.

Example 8: High Brightness + Custom Color (Honeycomb)
json
{
  "item": "minecraft:honeycomb",
  "treeStyle": "birch",
  "oreStyle": "gold",
  "lightLevel": 12,  // High brightness (close to maximum)
  "colors": "0xFFFFB6C1"  // Honeycomb pink
}
Note: Focuses on customizing lightLevel and colors, ideal for resource trees requiring glowing effects or thematic coloring.

Example 9: Invalid Block ID (Heavy Core) – Null Handling
json
{
  "item": "minecraft:heavy_core",
  "translateKey": "block.resource_farm.heavy_core_tree",
  // Intentionally use an invalid block ID → RegistriesUtils.getBlock returns null
  "customPlaceBlock": "minecraft:heavy_core_block", // Non-existent block
  "lightLevel": 3,
  "colors": "0xFF808080"  // Gray (metal theme)
}
Note: Demonstrates handling of invalid customPlaceBlock IDs. The code detects null and logs a warning without affecting registration (customPlaceBlock is set to null).

Example 10: Fertilization Configuration as NULL (Echo Shard) – Disable Fertilization
json
{
  "item": "minecraft:echo_shard",
  "treeStyle": "warped",
  "oreStyle": "netherite",
  // Set fertilization to null → disable fertilization (0% success rate)
  "fertilizeSetting": {
    "type": "null"
  },
  "customPlaceBlockTag": "minecraft:nether_ores",
  "lightLevel": 5,
  "colors": "0xFF000000"  // Black (Deep Dark theme)
}
Note: Demonstrates disabling fertilization by setting fertilizeSetting.Type to "null", ideal for resource trees that should not be fertilized.

Example 11: Special Block Configuration (Gilded Blackstone)
json
{
  "item": "minecraft:gilded_blackstone",
  "translateKey": "block.resource_farm.gilded_blackstone_tree",
  "customPlaceBlock": "minecraft:gilded_blackstone", // Valid block ID
  "lightLevel": 4,
  "colors": "0xFF8B4513"  // Golden brown (Gilded Blackstone theme)
}
Note: Demonstrates a valid customPlaceBlock configuration. The code correctly parses Gilded Blackstone and applies it to the resource tree's placement rules.

Example 12: Missing Core Fields (Respawn Anchor) – Trigger Skip Logic
json
{
  // Missing both item and translateKey → code logs a warning and skips this configuration
  "treeStyle": "crimson",
  "oreStyle": "netherite",
  "lightLevel": 10
}
Note: Intentionally omits both item and translateKey. The code identifies this as an invalid configuration, logs a warning, and skips registration without affecting other configurations.