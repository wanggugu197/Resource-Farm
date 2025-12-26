package com.resource_farm.common.pack;

import com.resource_farm.utils.RLUtils;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.IoSupplier;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenCustomHashMap;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ResourceFarmDynamicPackContents {

    private static final Hash.Strategy<String> STRING_HASH_STRATEGY = new Hash.Strategy<>() {

        @Override
        public int hashCode(String o) {
            return o.hashCode();
        }

        @Override
        public boolean equals(String a, String b) {
            return Objects.equals(a, b);
        }
    };

    private static class Node {

        private final Object2ObjectOpenCustomHashMap<String, Node> children = new Object2ObjectOpenCustomHashMap<>(
                STRING_HASH_STRATEGY);
        @Nullable
        private IoSupplier<InputStream> dataSupplier;

        void collectResources(String namespace, String[] pathComponents, int curIndex,
                              PackResources.ResourceOutput output) {
            if (curIndex >= pathComponents.length) {
                outputResources(namespace, String.join("/", pathComponents), output);
                return;
            }

            String component = pathComponents[curIndex];
            if (!component.isBlank()) {
                Node childNode = children.get(component);
                if (childNode != null) {
                    childNode.collectResources(namespace, pathComponents, curIndex + 1, output);
                }
            } else {
                collectResources(namespace, pathComponents, curIndex + 1, output);
            }
        }

        private boolean isTerminalNode() {
            return dataSupplier != null;
        }

        private Object2ObjectOpenCustomHashMap<String, Node> getChildren() {
            return children;
        }

        void outputResources(String namespace, String path, PackResources.ResourceOutput output) {
            if (isTerminalNode()) {
                output.accept(RLUtils.get(namespace, path),
                        Objects.requireNonNull(dataSupplier));
            }
            children.forEach((key, node) -> {
                String childPath = path.isBlank() ? key : path + "/" + key;
                node.outputResources(namespace, childPath, output);
            });
        }

        @Nullable
        Node getChild(String name) {
            return children.get(name);
        }

        void setTerminalData(IoSupplier<InputStream> supplier) {
            this.dataSupplier = supplier;
            this.children.clear();
        }
    }

    private final Node root = new Node();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void addToData(ResourceLocation location, byte[] bytes) {
        addToData(location, () -> new ByteArrayInputStream(bytes));
    }

    public void addToData(ResourceLocation location, IoSupplier<InputStream> supplier) {
        String[] pathComponents = Arrays.stream(location.getPath().split("/"))
                .filter(component -> !component.isBlank())
                .toArray(String[]::new);

        var writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Node currentNode = root.getChildren().computeIfAbsent(location.getNamespace(), k -> new Node());
            for (String component : pathComponents) {
                currentNode = currentNode.getChildren().computeIfAbsent(component, k -> new Node());
            }
            currentNode.setTerminalData(supplier);
        } finally {
            writeLock.unlock();
        }
    }

    public void clearData() {
        var writeLock = lock.writeLock();
        writeLock.lock();
        try {
            root.getChildren().clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Nullable
    public IoSupplier<InputStream> getResource(ResourceLocation location) {
        String[] pathComponents = Arrays.stream(location.getPath().split("/"))
                .filter(component -> !component.isBlank())
                .toArray(String[]::new);

        var readLock = lock.readLock();
        readLock.lock();
        try {
            Node currentNode = root.getChild(location.getNamespace());
            for (String component : pathComponents) {
                if (currentNode == null) break;
                currentNode = currentNode.getChild(component);
            }
            return currentNode == null ? null : currentNode.dataSupplier;
        } finally {
            readLock.unlock();
        }
    }

    public void listResources(String namespace, String path, PackResources.ResourceOutput resourceOutput) {
        String[] pathComponents = Arrays.stream(path.split("/"))
                .filter(component -> !component.isBlank())
                .toArray(String[]::new);

        var readLock = lock.readLock();
        readLock.lock();
        try {
            Node namespaceNode = root.getChild(namespace);
            if (namespaceNode != null) {
                namespaceNode.collectResources(namespace, pathComponents, 0, resourceOutput);
            }
        } finally {
            readLock.unlock();
        }
    }
}
