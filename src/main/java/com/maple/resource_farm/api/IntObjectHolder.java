package com.maple.resource_farm.api;

import java.util.Comparator;

public class IntObjectHolder<T> {

    public int number;
    public T obj;

    public IntObjectHolder(int number, T obj) {
        this.number = number;
        this.obj = obj;
    }

    private static class C implements Comparator<IntObjectHolder<?>> {

        @Override
        public int compare(IntObjectHolder<?> a, IntObjectHolder<?> b) {
            return Integer.compare(b.number, a.number);
        }
    }
}
