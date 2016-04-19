package tech.jonas.mondoandroid.utils;

import android.support.v4.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListUtils {

    public static <T> T first(List<T> list) {
        return list.get(0);
    }


    @SuppressWarnings("unchecked")
    public static <T, S> Map<T, S> toMap(List<Pair<T, S>> pairs) {
        final Map<T, S> pairMap = new HashMap<>();
        for (Pair<T, S> pair : pairs) {
            pairMap.put(pair.first, pair.second);
        }
        return pairMap;
    }
}
