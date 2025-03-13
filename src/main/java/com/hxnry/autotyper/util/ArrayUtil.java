package com.hxnry.autotyper.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Hxnry
 * @since November 08, 2016
 */
public class ArrayUtil {

    public static <T> T[] addToArray(T[] array, T e) {
        array  = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = e;
        return array;
    }



    public static int[] addToArray(int[] array, int e) {
        array  = Arrays.copyOf(array, array.length + 1);
        array[array.length - 1] = e;
        return array;
    }

    /**
    public static boolean arrayContainsAll(final List<Integer> list, Integer[] keys) {
        Integer[] toArray = list.toArray(Integer[]::new);
        Arrays.sort(toArray);
        return Stream.of(keys).allMatch(key -> Arrays.binarySearch(toArray, key) >= 0);
    }
     **/

    public static boolean arrayContains(final int[] array, final int key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) >= 0;
    }

    public static boolean arrayContains(final String[] array, final String key) {
        Arrays.sort(array);
        return Arrays.binarySearch(array, key) >= 0;
    }
}
