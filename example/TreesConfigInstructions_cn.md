# Resource Farm：用数据包构建资源树

树木、树皮样式与矿石叠加样式均通过 **数据包 JSON** 在启动时加载（扫描各模组 jar 内的 `data/`），不再使用 `config/` 下的注册/移除配置文件。

---

## 1. 总览

| 内容 | 路径模式 | 作用 |
|------|----------|------|
| **资源树定义** | `data/<namespace>/resource_farm_maps/resource_tree/**/*.json` | 注册一棵资源树（写入游戏内树木表） |
| **移除树木** | `data/<namespace>/resource_farm_maps/resource_tree_remove/**/*.json` | 在注册完成后移除指定树木 |
| **树基底样式** | `data/<namespace>/resource_farm_maps/tree_base_type/<path>.json` | 木头/树叶等基底模型与叠加纹理 |
| **矿叠加样式** | `data/<namespace>/resource_farm_maps/tree_extra_type/<path>.json` | 矿石/裂纹等叠加纹理 |

- 条目 ID = `namespace` + 文件相对路径（去掉 `.json`）  
  - 例：`data/minecraft/resource_farm_maps/tree_base_type/oak.json` → 样式 ID `minecraft:oak`  
  - 例：`data/mymod/resource_farm_maps/resource_tree/custom/paper.json` → 定义 ID `mymod:custom/paper`  
- 树的**游戏内方块 ID**仍由 `item` / `translate_key` 推导，例如 `minecraft:dirt` → `dirt_tree`。  
- 模组内置预设见：`src/main/resources/data/minecraft/resource_farm_maps/`。  
- 本目录示例见：[`datapack_sample/`](datapack_sample/)。

### 加载顺序

1. 扫描全部已加载模组 jar 中的样式与树定义  
2. 按 YAML 预设组开关过滤 `group`  
3. 写入树木注册表  
4. 应用 `resource_tree_remove`  
5. 再注册方块 / 物品  

因此：**其它模组或内置数据包**放进 jar 的 `data/` 即可；世界文件夹里的“后加数据包”不会参与这次启动期扫描（与配方式热重载不同）。

---

## 2. 注册一棵资源树

**路径：** `data/<namespace>/resource_farm_maps/resource_tree/<任意子路径>.json`

### 字段说明

| 字段 | 类型 | 默认 | 说明 |
|------|------|------|------|
| `item` | 字符串 | — | 关联物品 ID（与 `translate_key` **至少填一个**） |
| `translate_key` | 字符串 | — | 显示用翻译键；无 `item` 时也用于生成树 ID |
| `group` | 字符串 | 无 | 预设组：`base` / `mineral` / `biology` / `agriculture`。无 group 或其它值 = **始终加载**（适合附加包） |
| `automatic_basic_recipe` | bool | `true` | 是否自动生成基础产物/树苗配方 |
| `product_output` | int | `1` | 基础合成产量 |
| `tree_style` | 字符串 | `oak` | 树基底样式 ID（可写短名 `oak` → `minecraft:oak`） |
| `ore_style` | 字符串 | `iron` | 矿叠加样式 ID（短名同理） |
| `grower` | 字符串 | `oak` | 生长器 ID，决定使用哪套原版/自定义 configured feature |
| `fertilize` | 对象 | 骨粉默认 | 催熟，见下文 |
| `growth_frequency` | int | `10` | 随机刻生长：约 1/N 概率 |
| `custom_place_block` | 字符串 | — | 可种植的方块 ID |
| `custom_place_block_tag` | 字符串 | — | 可种植的方块 Tag |
| `light_level` | int | `0` | 发光 0–15（写入模型 light_emission） |
| `color` | int 或字符串 | `0` | 染色，如 `0x9E7255`、`#9E7255` 或十进制 |

### 示例 A：最简（仅物品）

```json
{
  "item": "minecraft:paper",
  "color": "0xF5F5DC"
}
```

这个文件会注册 `paper_tree`。未写字段会自动使用：

| 字段 | 实际使用值 |
|------|------------|
| `tree_style` | `oak` |
| `ore_style` | `iron` |
| `grower` | `oak` |
| `fertilize` | 骨粉，主/次成功率 `0.35` |
| `automatic_basic_recipe` | `true` |
| `product_output` | `1` |
| `growth_frequency` | `10` |
| `light_level` | `0` |

适合先确认加载链路正常，再逐步加外观、催熟、种植条件等配置。

### 示例 B：完整（下界之星主题）

```json
{
  "item": "minecraft:nether_star",
  "translate_key": "block.resource_farm.nether_star_tree",
  "automatic_basic_recipe": true,
  "product_output": 1,
  "tree_style": "oak",
  "ore_style": "emerald",
  "grower": "oak",
  "fertilize": {
    "main_item": "minecraft:blaze_powder",
    "main_chance": 0.3
  },
  "growth_frequency": 100,
  "custom_place_block": "minecraft:beacon",
  "light_level": 12,
  "color": "0xFFFFFF"
}
```

### 示例 C：无原版物品（仅翻译键）

```json
{
  "group": "base",
  "translate_key": "resource_farm.resource_tree.wood",
  "automatic_basic_recipe": false,
  "product_output": 1,
  "tree_style": "dark_oak",
  "ore_style": "nether_quartz",
  "color": "0xB08F55"
}
```

需在语言文件中提供 `resource_farm.resource_tree.wood` 等键（本模组已为内置预设注册一批）。

### 示例 D：自定义种植 Tag 与较慢生长

```json
{
  "item": "minecraft:amethyst_shard",
  "translate_key": "block.mymod.amethyst_shard_tree",
  "product_output": 4,
  "tree_style": "mymod:minimal_starwood",
  "ore_style": "mymod:minimal_gold",
  "fertilize": {
    "main_item": "minecraft:glowstone_dust",
    "main_chance": 0.2,
    "secondary_item": "minecraft:bone_meal",
    "secondary_chance": 0.05
  },
  "growth_frequency": 40,
  "custom_place_block_tag": "minecraft:crystal_sound_blocks",
  "light_level": 6,
  "color": "#B985FF"
}
```

- `custom_place_block_tag` 适合允许一组方块承载树苗；如果只允许一个方块，用 `custom_place_block`。
- `growth_frequency` 数值越大，随机刻成功越慢；`40` 大约是默认 `10` 的四分之一频率。
- `tree_style` / `ore_style` 使用的是示例目录里的部分字段样式文件，见第 5、6 节。

### 预设组 `group` 与配置开关

文件：`config/resource_farm/resource_farm_preset_tree.yml`（或模组 Configuration 生成的同名配置）

| `group` 值 | 对应开关 |
|------------|----------|
| `base` | `presetTreeGeneration.minecraftBase` |
| `mineral` | `minecraftMineral` |
| `biology` | `minecraftBiology` |
| `agriculture` | `minecraftAgriculture` |

- `enablePresetTreeGroups = false` 时，上述四个 group **全部跳过**。  
- **不写 `group`** 的定义视为附加内容，**不受**预设开关影响（推荐附加模组使用）。

---

## 3. 催熟 `fertilize`

### 快捷类型（优先，忽略其它字段）

```json
{ "type": "default" }
```

→ 骨粉，主/次成功率 0.35。

```json
{ "type": "null" }
```

→ 不可催熟。

### 自定义物品

```json
{
  "main_item": "minecraft:blaze_powder",
  "main_chance": 0.3,
  "secondary_item": "minecraft:bone_meal",
  "secondary_chance": 0.1
}
```

| 字段 | 说明 |
|------|------|
| `main_item` / `secondary_item` | 物品 ID |
| `main_chance` / `secondary_chance` | 0.0–1.0 |

---

## 4. 移除树木

**路径：** `data/<namespace>/resource_farm_maps/resource_tree_remove/<任意>.json`

在**全部树注册完成之后**执行。内容任选其一：

```json
"dirt_tree"
```

```json
{ "id": "dirt" }
```

```json
{ "tree_id": "dirt_tree" }
```

- 可写 `dirt` 或 `dirt_tree`（会自动补 `_tree`）。  
- 也可不写内容、仅用文件名：`resource_tree_remove/dirt.json` → 按文件名 `dirt` 移除。

树 ID 规则回顾：

- `item` = `minecraft:dirt` → id `dirt` → 注册键 **`dirt_tree`**  
- `item` = `mod:foo_bar` → `mod_foo_bar_tree`  
- 仅 `translate_key` = `a.b.wood` → 取最后一段 `wood` → **`wood_tree`**

---

## 5. 自定义树基底样式 `tree_base_type`

**路径：** `data/<namespace>/resource_farm_maps/tree_base_type/<path>.json`  
**引用：** 树定义里 `"tree_style": "oak"` 或 `"mymod:custom_wood"`

内置样式（`minecraft` 命名空间）：`oak`, `dark_oak`, `birch`, `spruce`, `jungle`, `acacia`, `cherry`, `mangrove`, `pale_oak`。

JSON 结构（嵌套，字段较多）：

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

- `models.*`：方块基底模型 ID（multipart 底层）。  
- `overlays.*`：着色层 / 物品叠图纹理路径。  
- `type`：生长器等逻辑用的种类名（如 `oak`）。  
- 无效 `tree_style` 会回退到默认橡木样式。
- `type`、`models`、`overlays`、`translate_keys` 以及它们的子字段都可以省略；省略时使用橡木/内置贴图/默认翻译键兜底。

完整内置文件可直接复制改路径：  
`src/main/resources/data/minecraft/resource_farm_maps/tree_base_type/`。

### 最小树基底样式示例

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

这个示例只改原木、横向原木、木板和树叶叠加层。其它字段，例如树苗模型、树脂/果实/碎块贴图、翻译键，都会回退到默认橡木配置。示例文件见 [`minimal_starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/minimal_starwood.json)。

### 示例文件

见 [`datapack_sample/data/mymod/resource_farm_maps/tree_base_type/starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/starwood.json)：

- 文件 → 样式 ID **`mymod:starwood`**
- 在树定义中引用：`"tree_style": "mymod:starwood"`
- 示例树：[`nether_star_themed.json`](datapack_sample/data/mymod/resource_farm_maps/resource_tree/nether_star_themed.json)（同时引用自定义 `mymod:star_ore`）

可把内置 `oak.json` / `dark_oak.json` 整份复制后只改 `models` 路径与 `type`，即可做出新木头外观。

---

## 6. 自定义矿叠加样式 `tree_extra_type`

**路径：** `data/<namespace>/resource_farm_maps/tree_extra_type/<path>.json`  
**引用：** `"ore_style": "iron"` 或 `"mymod:my_ore"`

```json
{
  "base": "resource_farm:block/ore/iron",
  "center": "resource_farm:block/ore/center"
}
```

| 字段 | 说明 |
|------|------|
| `base` | 主叠加纹理；缺省为 `resource_farm:block/ore/iron` |
| `center` | 中心层（如原木截面）；缺省为 `resource_farm:block/ore/center` |

最小示例：

```json
{
  "base": "resource_farm:block/ore/gold"
}
```

此时中心层自动使用默认 `resource_farm:block/ore/center`。示例文件见 [`minimal_gold.json`](datapack_sample/data/mymod/resource_farm_maps/tree_extra_type/minimal_gold.json)。

内置：`copper`, `diamond`, `emerald`, `gold`, `iron`, `lapis`, `nether_gold`, `nether_quartz`, `redstone`, `crack`。

---

## 7. 附加模组 / 数据包作者怎么做

1. 在你的模组资源中建立：

```text
src/main/resources/data/<你的modid>/resource_farm_maps/
  resource_tree/
    my_item.json
  tree_base_type/          # 可选：自定义木头外观
  tree_extra_type/         # 可选：自定义矿叠加
  resource_tree_remove/    # 可选：覆盖移除内置树
```

2. **不要**写 `group: base|mineral|...`，除非你希望受玩家预设开关控制。  
3. 引用本模组已有样式时可直接写 `"tree_style": "oak"`、`"ore_style": "diamond"`。  
4. 需要新外观时，先加 `tree_base_type` / `tree_extra_type`，再在树定义里用完整 ID：`"tree_style": "mymod:my_wood"`。  
5. 保证 jar 打进 `data/` 资源；启动游戏后看日志：

```text
[ResourceFarm] resource_farm_maps: ... base, ... extra, ... grower, ... tree json, ... remove
[ResourceFarm] Resource tree registration: applied N tree(s) (skipped M by preset), removed R.
```

---

## 8. 与旧版 Config JSON 的差异

| 旧（已废弃） | 新（数据包） |
|--------------|--------------|
| `config/resource_farm/resource_tree_register_configs.json` | `data/.../resource_farm_maps/resource_tree/*.json` |
| `resource_tree_remove_configs.json` | `resource_tree_remove/*.json` |
| 驼峰 `treeStyle` / `colors` | 蛇形 `tree_style` / `color` |
| `fertilizeSetting` | `fertilize`（支持 `type`） |
| 单文件数组 | **一树一文件** |

旧 config 文件**不再读取**。请迁移到数据包格式。

---

## 9. 示例文件索引

| 文件 | 说明 |
|------|------|
| [datapack_sample/...](datapack_sample/) | 可拷贝的示例布局（含 `tree_base_type` / `tree_extra_type`） |
| 树基底样式示例 | [`tree_base_type/starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/starwood.json) |
| 部分字段树基底示例 | [`tree_base_type/minimal_starwood.json`](datapack_sample/data/mymod/resource_farm_maps/tree_base_type/minimal_starwood.json) |
| 矿叠加样式示例 | [`tree_extra_type/star_ore.json`](datapack_sample/data/mymod/resource_farm_maps/tree_extra_type/star_ore.json) |
| 最小矿叠加示例 | [`tree_extra_type/minimal_gold.json`](datapack_sample/data/mymod/resource_farm_maps/tree_extra_type/minimal_gold.json) |
| 自定义种植 Tag 示例 | [`resource_tree/custom_soil_and_tag.json`](datapack_sample/data/mymod/resource_farm_maps/resource_tree/custom_soil_and_tag.json) |
| 模组内置预设 | `src/main/resources/data/minecraft/resource_farm_maps/` |

英文版：[TreesConfigInstructions_us.md](TreesConfigInstructions_us.md)
