package com.maple.resource_farm.common.Manager;

import com.maple.resource_farm.ResourceFarm;
import com.maple.resource_farm.ResourceTree.builder.TreeTagInsert;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态 Tag 注入（内存级，不经 JSON）。
 *
 * <h2>MC 26.1 Tag 加载链路（源码）</h2>
 * <ol>
 * <li>{@code WorldLoader.load} →
 * {@link net.minecraft.tags.TagLoader#loadTagsForExistingRegistries}
 * 对 STATIC 层（含 BLOCK/ITEM）调用 {@code loadPendingTags}</li>
 * <li>{@code loadPendingTags}：
 * {@code loader.load(manager)} 读 JSON →
 * {@code loader.build(...)} 将 {@link net.minecraft.tags.TagEntry} 解析为 {@link Holder} →
 * {@code wrapTags} → {@link Registry#prepareTagReload}</li>
 * <li>稍后 {@code ReloadableServerResources#updateComponentsAndStaticRegistryTags}
 * 调用 {@code PendingTags#apply()} →
 * {@link net.minecraft.core.MappedRegistry#bindTags} /
 * {@code HolderSet.Named#bind} → 写入注册表并刷新 Holder 上的 tag 反向索引</li>
 * <li>NeoForge：{@code TagsUpdatedEvent.ServerDataLoad}（apply 之后）；
 * {@code PendingTagsExtension#contents()} 可在 apply 前窥视，但返回不可变 Map，不宜作为注入点</li>
 * </ol>
 *
 * <h2>本模组注入点</h2>
 * 在 {@code TagLoader#build} 的 RETURN 处，把已解析好的 {@link Holder} 直接合并进
 * {@code Map<Identifier, List<Holder>>}。随后仍走原版 {@code prepareTagReload → apply}，
 * <b>不</b>写 data/.../tags/*.json，也<b>不</b>再走 TagEntry 字符串解析。
 *
 * <p>
 * 对比旧路径（{@code TagLoader#load} RETURN 注入 {@code EntryWithSource}）：
 * 新路径在 build 之后注入对象，少一次 id→Holder 查找失败风险，语义更接近「直接塞进注册表」。
 */
public class ResourceFarmTagManager {

    /**
     * 将动态成员以 {@link Holder} 形式合并进 build 结果。
     *
     * @param built    {@link net.minecraft.tags.TagLoader#build} 的返回值（可能含不可变 List）
     * @param registry 当前正在加载标签的注册表（由 mixin ThreadLocal 传入）
     * @return 若无需修改则返回原 map；否则返回新的可变副本
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map injectBuiltHolders(Map built, Registry<?> registry) {
        if (built == null || registry == null) {
            return built;
        }

        Map<Identifier, List<Identifier>> additions;
        if (registry == BuiltInRegistries.BLOCK) {
            additions = TreeTagInsert.collectBlockTagMembers();
        } else if (registry == BuiltInRegistries.ITEM) {
            additions = TreeTagInsert.collectItemTagMembers();
        } else {
            return built;
        }

        if (additions.isEmpty()) {
            return built;
        }

        Map out = new HashMap(built);
        int added = 0;
        for (Map.Entry<Identifier, List<Identifier>> e : additions.entrySet()) {
            Identifier tagId = e.getKey();
            List merged = new ArrayList((List) out.getOrDefault(tagId, List.of()));
            for (Identifier elementId : e.getValue()) {
                var opt = registry.get(elementId);
                if (opt.isEmpty()) {
                    ResourceFarm.LOGGER.warn(
                            "[ResourceFarm] Tag inject skipped missing element {} for tag {} in {}",
                            elementId, tagId, registry.key().identifier());
                    continue;
                }
                Holder.Reference<?> holder = opt.get();
                if (!merged.contains(holder)) {
                    merged.add(holder);
                    added++;
                }
            }
            // prepareTagReload 会 List.copyOf，这里保持可变 List 亦可
            out.put(tagId, List.copyOf(merged));
        }

        if (added > 0) {
            ResourceFarm.LOGGER.info(
                    "[Resource Farm] Injected {} Holder(s) into {} tags for registry {}",
                    added, additions.size(), registry.key().identifier());
        }
        return out;
    }

    // ====================== 工具：收集 id（供 TreeTagInsert 使用） ======================

    public static void addMember(Object2ObjectOpenHashMap<Identifier, List<Identifier>> map,
                                 Identifier tagId, Identifier elementId) {
        if (tagId == null || elementId == null) return;
        map.computeIfAbsent(tagId, k -> new ArrayList<>()).add(elementId);
    }
}
