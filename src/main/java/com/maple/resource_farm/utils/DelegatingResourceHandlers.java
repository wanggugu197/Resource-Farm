package com.maple.resource_farm.utils;

import net.neoforged.neoforge.transfer.DelegatingResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.resource.Resource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;

import org.jspecify.annotations.NonNull;

public class DelegatingResourceHandlers {

    /** 允许 insert，拒绝 extract（不改 UI 直连 handler 的行为）。 */
    public static final class InsertOnly<T extends Resource> extends DelegatingResourceHandler<T> {

        public InsertOnly(ResourceHandler<T> delegate) {
            super(delegate);
        }

        @Override
        public int extract(int index, @NonNull T resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }

        @Override
        public int extract(@NonNull T resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }
    }

    /** 允许 extract，拒绝 insert（不改 UI 直连 handler 的行为）。 */
    public static final class ExtractOnly<T extends Resource> extends DelegatingResourceHandler<T> {

        public ExtractOnly(ResourceHandler<T> delegate) {
            super(delegate);
        }

        @Override
        public int insert(int index, @NonNull T resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }

        @Override
        public int insert(@NonNull T resource, int amount, @NonNull TransactionContext transaction) {
            return 0;
        }
    }
}
