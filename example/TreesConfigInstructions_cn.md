# 如何通过Config注册资源树
| 核心字段	                | 作用	                              | 取值示例	                                   | 默认值    |
|----------------------|----------------------------------|-----------------------------------------|--------|
| item	                | 资源树关联的物品 ID（与 translateKey 二选一）	 | "minecraft:nether_star"	                | null   |
| translateKey	        | 翻译键（与 item 二选一） 同时输入优先使用翻译键显示	   | "block.resource_farm.nether_star_tree"	 | null   |
| productOutput	       | 资源树基础合成产量	                       | 8/16	                                   | 1      |
| treeStyle	           | 树样式（对应 ResourceTreeTypes）	       | "oak"/"spruce"/"birch"	                 | "oak"  |
| oreStyle	            | 矿石样式（对应 ResourceOreTypes）	       | "iron"/"diamond"/"netherite"	           | "iron" |
| fertilizeSetting	    | 催熟配置（嵌套对象，参考 FertilizeSettings）	 | 见示例	                                    | 默认催熟配置 |
| growthFrequency	     | 生成频率（每个随机刻，会有 其分之一 概率长大）	        | 5/10	                                   | 10     |
| customPlaceBlock	    | 自定义放置方块 ID	                      | "minecraft:netherrack"	                 | null   |
| customPlaceBlockTag	 | 自定义放置方块标签	                       | "minecraft:nether_blocks"	              | null   |
| lightLevel	          | 资源树亮度（0-15，Minecraft 亮度范围）	      | 10/15	                                  | 0      |
| colors	              | 资源树颜色（16 进制整数，如 0xFF0000 = 红色）	  | 0xFF5500/0x00FFFF	                      | 0      |

### 示例 1：完整配置（下界之星）—— 所有字段自定义
```
{
  // 核心字段：item（优先用于生成ID）
  "item": "minecraft:nether_star",
  // 翻译键（备用ID生成，也用于游戏内翻译）
  "translateKey": "block.resource_farm.nether_star_tree",
  // 树样式：自定义为绯红森林样式（若存在该枚举值，否则兜底为oak）
  "treeStyle": "crimson",
  // 矿石样式：自定义为下界之星专属（无则兜底为iron）
  "oreStyle": "nether_star",
  // 催熟配置：自定义主/次要催熟物品+成功率
  "fertilizeSetting": {
    "mainItem": "minecraft:glow_berries", // 主催熟物品：发光浆果
    "mainChance": 0.5,                    // 主成功率：50%
    "secondaryItem": "minecraft:nether_wart", // 次要催熟物品：下界疣
    "secondaryChance": 0.2                 // 次要成功率：20%
  },
  // 生成频率：每个随机刻有1/100概率长大
  "growthFrequency": 100
  // 自定义放置方块：下界岩
  "customPlaceBlock": "minecraft:netherrack",
  // 自定义放置方块Tag：下界方块标签（示例）
  "customPlaceBlockTag": "minecraft:nether_blocks",
  // 发光亮度：10（中等亮度）
  "lightLevel": 10,
  // 颜色：品红色（0xFF800080）
  "colors": "0xFF800080"
}
```
注释：完整配置演示，所有字段自定义，无默认值兜底，适合需要精准控制的资源树。

### 示例 2：部分字段缺失（海洋之心）—— 依赖默认值
```
{
  "item": "minecraft:heart_of_the_sea",
  "translateKey": "block.resource_farm.heart_of_the_sea_tree",
  // 缺失treeStyle → 默认oak
  // 缺失oreStyle → 默认iron
  // 缺失fertilizeSetting → 默认骨粉35%成功率
  // 缺失customPlaceBlock → null
  // 缺失customPlaceBlockTag → null
  "lightLevel": 8, // 仅自定义亮度
  "colors": "0xFF0000FF" // 仅自定义蓝色（海洋主题）
}
```
注释：演示 “必填字段（item/translateKey）填，其余缺省” 的场景，缺失字段自动用默认值，简化配置。

### 示例 3：自定义 Tag + 枚举（紫水晶）—— 用 Tag 替代 Block
```
{
  "item": "minecraft:amethyst_shard",
  "productOutput": 16
  "treeStyle": "azalea", // 杜鹃树样式（若枚举存在）
  "oreStyle": "amethyst", // 紫水晶矿石样式
  // 不填customPlaceBlock，改用Tag指定可放置方块
  "customPlaceBlockTag": "minecraft:stone_ore_replaceables",
  "lightLevel": 7,
  "colors": "0xFF9966CC" // 紫水晶色
}
```
注释：演示 “用 customPlaceBlockTag 替代 customPlaceBlock” 的场景，适合批量指定可放置方块（如所有可替换的石头）。

### 示例 4：无效值场景（锻造模板）—— 展示兜底行为
```
{
  "item": "minecraft:smithing_template",
  "treeStyle": "invalid_tree_style", // 无效树样式 → 兜底为oak
  "oreStyle": "invalid_ore_style",   // 无效矿石样式 → 兜底为iron
  "customPlaceBlock": "minecraft:sculk_shrieker", // 尖啸体
  "lightLevel": 20, // 超出亮度范围（0-15）→ 仍按20传入（游戏内会自动限制）
  "colors": "-1" // 负数颜色值 → 按实际数值传入（ColoringSettings会处理）
}
```
注释：故意填无效值，演示代码的兜底逻辑：无效枚举→默认值、无效方块 ID→null、数值超限→原样传入。

### 示例 5：仅填 translateKey（末影珍珠）—— 无 item 场景
```
{
  // 无item，仅填translateKey（用于生成ID）
  "translateKey": "block.resource_farm.ender_pearl_tree",
  "treeStyle": "dark_oak",
  "oreStyle": "ender",
  "fertilizeSetting": {
    "type": "default" // 直接用默认催熟配置
  },
  "customPlaceBlock": "minecraft:end_stone",
  "lightLevel": 15, // 最大亮度
  "colors": "0xFF000000" // 黑色（末影主题）
}
```
注释：演示 “item 为 null，仅用 translateKey” 的场景，代码会用 translateKey 生成 ID（截取最后一个。后的部分）。

### 示例 6：仅填 item（纸）—— 极简核心配置
```
{
  // 仅填item，其余全缺省
  "item": "minecraft:paper"
  // 缺失translateKey → 用item生成ID（paper）
  // 缺失treeStyle → oak
  // 缺失oreStyle → iron
  // 缺失fertilizeSetting → 默认骨粉配置
  // 缺失customPlaceBlock/customPlaceBlockTag → null
  // 缺失lightLevel → 0
  // 缺失colors → 0
}
```
注释：最简化配置，仅填必填的 item，其余全靠默认值，适合快速注册基础资源树。

### 示例 7：自定义催熟配置（不死图腾）—— 特殊催熟规则
```
{
  "item": "minecraft:totem_of_undying",
  "translateKey": "block.resource_farm.totem_tree",
  // 自定义催熟配置：仅主物品，无次要物品
  "fertilizeSetting": {
    "mainItem": "minecraft:gold_ingot", // 主催熟物品：金锭
    "mainChance": 0.8                   // 成功率80%
  },
  "customPlaceBlock": "minecraft:gold_blocks",  // 无效方块ID → 解析为null
  "lightLevel": 9,
  "colors": "0xFFFFD700" // 金色（不死图腾主题）
}
```
注释：演示 “仅主催熟物品” 的配置，secondaryItem 缺省时，FertilizeSettings 会自动设为 null+0% 成功率。

### 示例 8：高亮度 + 自定义颜色（蜜脾）
```
{
  "item": "minecraft:honeycomb",
  "treeStyle": "birch",
  "oreStyle": "gold",
  "lightLevel": 12, // 高亮度（接近最大值）
  "colors": "0xFFFFB6C1" // 蜜脾粉色
}
```
注释：重点演示 lightLevel 和 colors 的自定义，适合有发光 / 配色需求的资源树。

### 示例 9：无效方块 ID（沉重核心）—— 展示 null 处理
```
{
  "item": "minecraft:heavy_core",
  "translateKey": "block.resource_farm.heavy_core_tree",
  // 故意填无效方块ID → RegistriesUtils.getBlock返回null
  "customPlaceBlock": "minecraft:heavy_core_block", // 无此方块
  "lightLevel": 3,
  "colors": "0xFF808080" // 灰色（金属主题）
}
```
注释：演示无效 customPlaceBlock 的场景，代码会检测到 null 并打印日志，不影响注册（customPlaceBlock 设为 null）。

### 示例 10：催熟配置为 NULL（回响碎片）—— 禁用催熟
```
{
  "item": "minecraft:echo_shard",
  "treeStyle": "warped",
  "oreStyle": "netherite",
  // 催熟配置设为null → 禁用催熟（成功率0%）
  "fertilizeSetting": {
    "type": "null"
  },
  "customPlaceBlockTag": "minecraft:nether_ores",
  "lightLevel": 5,
  "colors": "0xFF000000" // 黑色（深暗主题）
}
```
注释：演示通过 fertilizeSetting 的 type="null" 禁用催熟，适合无需催熟的资源树。

### 示例 11：特殊方块配置（镶金黑石）
```
{
  "item": "minecraft:gilded_blackstone",
  "translateKey": "block.resource_farm.gilded_blackstone_tree",
  "customPlaceBlock": "minecraft:gilded_blackstone", // 有效方块ID
  "lightLevel": 4,
  "colors": "0xFF8B4513" // 金棕色（镶金黑石主题）
}
```
注释：演示有效 customPlaceBlock 的配置，代码会正确解析为镶金黑石方块，用于资源树的放置规则。

### 示例 12：核心字段都缺失（重生锚）—— 触发跳过逻辑
```
{
  // 缺失item和translateKey → 代码检测到后打印warn并跳过该配置
  "treeStyle": "crimson",
  "oreStyle": "netherite",
  "lightLevel": 10
}
```
注释：故意不填 item 和 translateKey，代码会判定为无效配置，打印警告并跳过注册，不影响其他配置。

---

# 如何通过Config移除资源树
在注册资源树时，系统会根据输入的 `item` 和 `translateKey` 两个参数**自动生成 treeId**，移除资源树的核心就是将生成的 treeId 填入对应 `.json` 文件。

### treeId 生成规则（核心）
treeId 的生成逻辑分两种核心场景，优先级为：`item` 参数优先于 `translateKey` 参数。

#### 场景 1：输入了 `item` 参数
根据 `item` 对应的 `namespace`（命名空间）是否为 `minecraft`，生成规则不同：
- 若 `namespace` = `minecraft`： `treeId = [path] + "_tree"`
- 若 `namespace` ≠ `minecraft`： `treeId = [namespace] + "_" + [path] + "_tree"`

#### 场景 2：未输入 `item` 参数
此时会基于 `translateKey` 生成：`treeId = (translateKey 最后一个 "." 后面的部分) + "_tree"`

#### 移除资源树的操作步骤
将上述规则生成的 **treeId** 准确填入对应的 `.json` 配置文件中，即可完成该资源树的移除。

1. 示例1（输入item，namespace为minecraft）：
    - item参数：`minecraft:stick`（namespace = minecraft，path = stick）
    - 生成的treeId：`stick_tree`
2. 示例2（输入item，namespace不为minecraft）：
   - item参数：`create:andesite_alloy`（namespace = create，path = andesite_alloy）
   - 生成的treeId：`create_andesite_alloy_tree`
3. 示例2（未输入item，仅translateKey）：
    - translateKey：`gui.example.resource_tree`
    - 取最后一个"."后的部分：`resource_tree`
    - 生成的treeId：`resource_tree_tree`
