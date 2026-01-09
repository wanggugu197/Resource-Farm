package com.resource_farm.utils;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Array;

public class RFArrayUtils {

    public static Object[] concatenateArrays(Object... elements) {
        Object[] result = new Object[0];
        for (Object elem : elements) {
            if (elem == null) continue;
            if (elem.getClass().isArray()) {
                Object[] arrayElems = new Object[Array.getLength(elem)];
                for (int i = 0; i < arrayElems.length; i++) arrayElems[i] = Array.get(elem, i);
                result = ArrayUtils.addAll(result, arrayElems);
            } else {
                result = ArrayUtils.addAll(result, elem);
            }
        }

        return result;
    }

    public static Object[] insertCharBeforeElement(Object inputArray) {
        if (inputArray == null || !inputArray.getClass().isArray()) return new Object[0];
        int inputLength = Array.getLength(inputArray);
        if (inputLength == 0) return new Object[0];
        Object[] result = new Object[inputLength * 2];
        char currentChar = 'A';
        for (int i = 0; i < inputLength; i++) {
            result[2 * i] = currentChar;
            result[2 * i + 1] = Array.get(inputArray, i);
            currentChar++;
        }
        return result;
    }
}
