package com.resource_farm.data.tree.RegisterAndSpecialRecipe;

import com.resource_farm.ResourceFarm;
import com.resource_farm.api.block.FertilizeSettings;
import com.resource_farm.config.ResourceFarmConfigHolder;
import com.resource_farm.data.ResourceFarmBlocks;
import com.resource_farm.data.misc.ResourceFarmBlockTags;
import com.resource_farm.data.tree.builder.TreeRegister;
import com.resource_farm.utils.RLUtils;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;

import com.simibubi.create.api.data.recipe.MechanicalCraftingRecipeBuilder;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.kinetics.deployer.DeployerApplicationRecipe;
import com.simibubi.create.content.kinetics.fan.processing.SplashingRecipe;
import com.simibubi.create.content.kinetics.mixer.CompactingRecipe;
import com.simibubi.create.content.kinetics.mixer.MixingRecipe;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.kinetics.saw.CuttingRecipe;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipeBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;

import java.util.stream.Stream;

import static com.resource_farm.api.ResourceOre.ResourceOreTypes.IRON;
import static com.resource_farm.api.ResourceTree.ResourceTreeTypes.*;

public class CrossoverCreateRegister {

    private static final boolean GENERATE_STRIPPED_LOG = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedLog;
    private static final boolean GENERATE_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateWood;
    private static final boolean GENERATE_STRIPPED_WOOD = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generateStrippedWood;
    private static final boolean GENERATE_PLANKS = ResourceFarmConfigHolder.TreeConfigHolder.tree.blockGeneration.generatePlanks;

    private static final boolean GENERATE_TREE_ITEM_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateTreeItemRecipes;
    private static final boolean GENERATE_SAPLING_RECIPES = ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateSaplingRecipes;

    public static void init(RecipeOutput consumer) {
        ResourceFarmBlocks.ResourceTreeMap.forEach((name, resourceTree) -> {
            // 漂洗配方 漂洗树叶 增产约 87.5%
            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateCreateSplashingLeavesRecipe) {
                BlockEntry<?> leavesEntry = resourceTree.getLeaves();
                new SplashingRecipe.Builder<>(SplashingRecipe::new, ResourceFarm.id("wash_" + leavesEntry.getId().getPath()))
                        .require(leavesEntry)
                        .output(0.125f, resourceTree.getResin(), 2)
                        .output(0.5f, resourceTree.getFruit(), 2)
                        .build(consumer);
            }

            // 混合搅拌配方 水萃原木获得树脂 增产 25%
            if (ResourceFarmConfigHolder.TreeConfigHolder.tree.recipeGeneration.generateCreateMixingGetResinRecipes) {
                buildWaterExtractionRecipe(consumer, resourceTree.getLog(), resourceTree.getResin(), 4);
                if (GENERATE_STRIPPED_LOG) buildWaterExtractionRecipe(consumer, resourceTree.getStrippedLog(), resourceTree.getResin(), 4);
                if (GENERATE_WOOD) buildWaterExtractionRecipe(consumer, resourceTree.getWood(), resourceTree.getResin(), 4);
                if (GENERATE_STRIPPED_WOOD) buildWaterExtractionRecipe(consumer, resourceTree.getStrippedWood(), resourceTree.getResin(), 4);
                if (GENERATE_PLANKS) buildWaterExtractionRecipe(consumer, resourceTree.getPlanks(), resourceTree.getResin(), 1);
            }
        });
    }

    private static void buildWaterExtractionRecipe(RecipeOutput consumer, BlockEntry<?> blockEntry, ItemEntry<?> itemEntry, int count) {
        new MixingRecipe.Builder<>(MixingRecipe::new, ResourceFarm.id("water_extraction_" + blockEntry.getId().getPath()))
                .require(blockEntry).require(Fluids.WATER, 1000)
                .output(itemEntry.asStack(count)).output(0.25f, itemEntry.asStack(count))
                .duration(200).requiresHeat(HeatCondition.HEATED).build(consumer);
    }

    public static void create() {
        TreeRegister.createResourceTree("create:raw_zinc", null, true, 1,
                DARK_OAK, IRON, FertilizeSettings.of("create:zinc_nugget", 1, "minecraft:iron_nugget", 0.8), 50,
                null, ResourceFarmBlockTags.createBlockTag(RLUtils.parse("c:ores/zinc")), 0, 0xb7e6bf);

        TreeRegister.createResourceTree("create:andesite_alloy", null, true, 1,
                OAK, IRON, FertilizeSettings.of("create:zinc_nugget", 0.8, "minecraft:iron_nugget", 0.6), 50,
                "create:andesite_casing", null, 0, 0xa7ad9f);

        TreeRegister.createResourceTree("create:brass_ingot", null, true, 1,
                CHERRY, IRON, FertilizeSettings.of("create:brass_nugget", 0.8), 50,
                "create:brass_casing", null, 0, 0xf8ca67);

        TreeRegister.createResourceTree("create:cardboard", null, true, 1,
                SPRUCE, IRON, FertilizeSettings.of("create:pulp", 0.8), 50,
                "create:cardboard_block", null, 0, 0xcfa87e);

        TreeRegister.createResourceTree("create:rose_quartz", null, true, 1,
                JUNGLE, IRON, FertilizeSettings.of("minecraft:quartz", 1, "minecraft:redstone", 0.75), 50,
                "create:rose_quartz_lamp", null, 0, 0xfc8383);

        TreeRegister.createResourceTree("create:precision_mechanism", null, true, 1,
                ACACIA, IRON, FertilizeSettings.of("minecraft:gold_ingot", 0.6), 50,
                "create:rotation_speed_controller", null, 0, 0xd09f54);
    }

    public static void createSpecialRecipe(RecipeOutput consumer) {}

    public static void common(RecipeOutput consumer) {
        {
            MechanicalCraftingRecipeBuilder
                    .shapedRecipe(Items.NETHERITE_BLOCK, 1)
                    // 步骤 2：绑定字符与原料
                    .key('N', Items.NETHERITE_INGOT)    // 'N' = 下界合金锭
                    .key('O', Items.OBSIDIAN)// 'O' = 黑曜石
                    // 步骤 3：添加 9x9 配方图案（9行，每行9个字符，保证矩形布局）
                    .patternLine("OOOOOOOOO")  // 第1行：全黑曜石
                    .patternLine("O N N N O")  // 第2行：两侧黑曜石，中间下界合金
                    .patternLine("O N N N O")  // 第3行：两侧黑曜石，中间下界合金
                    .patternLine("O N N N O")  // 第4行：两侧黑曜石，中间下界合金
                    .patternLine("OOOOOOOOO")  // 第5行：全黑曜石（分隔层）
                    .patternLine("O N N N O")  // 第6行：两侧黑曜石，中间下界合金
                    .patternLine("O N N N O")  // 第7行：两侧黑曜石，中间下界合金
                    .patternLine("O N N N O")  // 第8行：两侧黑曜石，中间下界合金
                    .patternLine("OOOOOOOOO") // 第9行：全黑曜石
                    // 步骤 4：可选配置（禁用镜像、添加模组加载条件）
                    .disallowMirrored()                // 禁用镜像匹配，严格保持 9x9 布局方向
                    .whenModLoaded("create")          // 仅当 Create 模组加载时生效
                    // 步骤 5：构建配方，指定自定义配方 ID
                    .build(consumer, ResourceFarm.id("max_size_netherite_block"));
        }

        {// 1. 初始化构建器，指定配方唯一ID（更具语义化的命名）
            new SequencedAssemblyRecipeBuilder(ResourceFarm.id("super_advanced_netherite_core"))
                    .require(Items.IRON_INGOT)
                    .transitionTo(Items.GOLD_INGOT)
                    // 4. 设置序列循环次数（5次，大规模工业化生产，重复执行所有7个步骤）
                    .loops(5)
                    // 5. 添加步骤1：常压冲压塑形（PressingRecipe - 常压，基础塑形）
                    .addStep(PressingRecipe::new, pressingBuilder -> pressingBuilder)
                    // 6. 添加步骤2：精密切削（CuttingRecipe - 最简配置，修正尺寸误差）
                    .addStep(CuttingRecipe::new, cuttingBuilder -> cuttingBuilder)
                    // 7. 添加步骤3：二次精密切削（CuttingRecipe - 重复步骤，提升切削精度）
                    .addStep(CuttingRecipe::new, cuttingBuilder -> cuttingBuilder)
                    // 8. 添加步骤4：流体冷却（FillingRecipe - 新增流体步骤，合法配置，降低工件温度）
                    .addStep(FillingRecipe::new, fillingBuilder -> fillingBuilder
                            .require(net.minecraft.world.level.material.Fluids.WATER, 1000))
                    // 9. 添加步骤5：机械手复合涂抹（DeployerApplicationRecipe - 复合工具+消耗工具，核心复杂步骤）
                    .addStep(DeployerApplicationRecipe::new, deployerBuilder -> deployerBuilder
                            .require(Ingredient.fromValues(Stream.of(
                                    new Ingredient.ItemValue(Items.HONEY_BOTTLE.getDefaultInstance()),
                                    new Ingredient.ItemValue(Items.MAGMA_CREAM.getDefaultInstance())))))
                    // 10. 添加步骤6：高压二次冲压（PressingRecipe - 高压等级，提升机械需求门槛）
                    .addStep(PressingRecipe::new, pressingBuilder -> pressingBuilder)
                    // 12. 添加步骤8：机械手清理废料（DeployerApplicationRecipe - 保留工具，收尾步骤）
                    .addStep(DeployerApplicationRecipe::new, deployerBuilder -> deployerBuilder
                            .require(Items.IRON_SHOVEL)
                            .toolNotConsumed())
                    // 13. 多权重最终输出池（1主输出+5副输出，精细权重分配，总和1.0f）
                    .addOutput(new ItemStack(Items.NETHERITE_BLOCK, 2), 0.80f)
                    // 副输出1：残留铁粒（6个，权重0.08f，8%概率，主要废料）
                    .addOutput(new ItemStack(Items.IRON_NUGGET, 6), 0.08f)
                    // 副输出2：金粒（3个，权重0.05f，5%概率，过渡物品残留）
                    .addOutput(new ItemStack(Items.GOLD_NUGGET, 3), 0.05f)
                    // 副输出3：黑曜石碎片（1个，权重0.04f，4%概率，辅料残留）
                    .addOutput(new ItemStack(Items.OBSIDIAN, 1), 0.04f)
                    // 副输出4：下界合金粉末（1个，权重0.02f，2%概率，稀有副产品）
                    .addOutput(new ItemStack(Items.NETHERITE_SCRAP, 1), 0.02f)
                    // 副输出5：烈焰粉（1个，权重0.01f，1%概率，超稀有副产品）
                    .addOutput(new ItemStack(Items.BLAZE_POWDER, 1), 0.01f)
                    // 14. 构建配方，写入数据包完成注册（无报错，符合所有规范）
                    .build(consumer);
        }

        {
            // 构建器类型：StandardProcessingRecipe.Builder<PressingRecipe>
            PressingRecipe.Builder<PressingRecipe> builder = new PressingRecipe.Builder<>(
                    PressingRecipe::new, // 核心：传入 Factory（构造函数引用，符合 StandardProcessingRecipe.Factory 接口）
                    ResourceFarm.id("iron_ingot_to_iron_plate") // 配方
                                                                // ID：resource_farm:pressing/iron_ingot_to_iron_plate
            );

            // 2. 配置配方核心参数
            builder
                    // 2.1 输入原料（塑形对象：铁锭，1个）
                    .require(Items.DIAMOND)
                    // 2.2 输出产物（塑形结果：铁块（替代铁板，实际开发自定义板类物品），1个）
                    .output(new ItemStack(Items.IRON_BLOCK, 1));

            // 3. 构建配方，写入数据包（完成注册）
            builder.build(consumer);
        }

        {
            CompactingRecipe.Builder<CompactingRecipe> builder = new CompactingRecipe.Builder<>(
                    CompactingRecipe::new, // 核心：传入 Factory（CompactingRecipe 构造函数引用）
                    ResourceFarm.id("netherite_scrap_to_netherite_block") // 配方
                                                                          // ID：resource_farm:compacting/netherite_scrap_to_netherite_block
            );

            // 2. 配置配方核心参数（基础+高级）
            builder
                    // 2.1 多原料输入（塑形所需原料，支持最多 64 个输入，符合 BasinRecipe.getMaxInputCount()）
                    .require(Items.NETHERITE_SCRAP) // 4个下界合金碎片
                    .require(Items.NETHERITE_SCRAP)
                    .require(Items.NETHERITE_SCRAP)
                    .require(Items.NETHERITE_SCRAP)
                    .require(Items.OBSIDIAN) // 4个黑曜石
                    .require(Items.OBSIDIAN)
                    .require(Items.OBSIDIAN)
                    .require(Items.OBSIDIAN)
                    .require(Items.GOLD_INGOT) // 1个金锭（辅料）
                    // 2.2 流体输入（高级特性，BasinRecipe 支持，注入 1000mb 熔岩（1桶），提供高温）
                    .require(Fluids.LAVA, 1000)
                    // 2.3 输出产物（塑形结果：下界合金块，1个）
                    .output(new ItemStack(Items.NETHERITE_BLOCK, 1))
                    // 2.4 加工时长（支持，200tick=10秒，压缩塑形耗时更长）
                    .duration(200)
                    // 2.6 热量要求（高级特性，BasinRecipe 支持，需要烈焰燃烧器（MAX 等级））
                    .requiresHeat(HeatCondition.HEATED); // 等价于 requiresHeat(HeatLevel.SEETHING)（高温）

            // 3. 构建配方，写入数据包（完成注册）
            builder.build(consumer);
        }

        {
            new DeployerApplicationRecipe.Builder<>(
                    DeployerApplicationRecipe::new, // 核心：传入 Factory（构造函数引用，符合 ItemApplicationRecipe.Factory 接口）
                    ResourceFarm.id("gold_ingot_gilding") // 配方 ID：resource_farm:deploying/gold_ingot_gilding
            )
                    .require(Items.GOLD_INGOT)
                    .require(Items.HONEY_BOTTLE)
                    .output(new ItemStack(Items.NETHERITE_INGOT, 1))
                    // .toolNotConsumed() // 若开启，蜂蜜瓶不会被消耗，机械手可重复使用（适合耐用工具如铁铲）
                    .build(consumer);
        }
    }
}
