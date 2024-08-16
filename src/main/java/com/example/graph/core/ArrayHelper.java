package com.example.graph.core;

import java.util.Comparator;

public class ArrayHelper {

    public static int linearSearch(Object[] array, int from, int to, Object o) {
        if (o == null) {
            for (int i = from; i < to; i++) {
                if (null == array[i]) {
                    return i;
                }
            }
        } else {
            for (int i = from; i < to; i++) {
                if (o.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static <T> int binarySearch(T[] array, int low, int high, Comparator<? super T> comparator, T key) {
        while (low < high) {
            int mid = (low + high - 1) >>> 1;
            T value = array[mid];
            int cmp = comparator.compare(value, key);
            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid;
            } else {
                return mid;
            }
        }
        return -(low + 1);
    }

    public static void fill(Object[] array, int from, int to, Object o) {
        for (int i = from; i < to; i++) {
            array[i] = o;
        }
    }

    public static int calculateNewLength(int minCapacity) {
        return minCapacity < 16
                ? minCapacity << 1
                : minCapacity + (minCapacity >>> 1);
    }

    public static Object[] copy(Object[] array, int size, int length) {
        Object[] newArray = new Object[length];
        System.arraycopy(array, 0, newArray, 0, size);
        return newArray;
    }

    public static void insert(Object[] array, int size, Object o, int index) {
        shiftRight(array, index, size);
        array[index] = o;
    }

    public static void remove(Object[] array, int last, int index) {
        shiftLeft(array, index, last);
        array[last] = null;
    }

    private static void shiftLeft(Object[] array, int from, int to) {
        System.arraycopy(array, from + 1, array, from, to - from);
    }

    private static void shiftRight(Object[] array, int from, int to) {
        System.arraycopy(array, from, array, from + 1, to - from);
    }

    public static void shiftAndSwapLeft(Object[] array, int from, int to) {
        Object o = array[from];
        shiftLeft(array, from, to);
        array[to] = o;
    }

    public static void shiftAndSwapRight(Object[] array, int from, int to) {
        Object o = array[to];
        shiftRight(array, from, to);
        array[from] = o;
    }
}
