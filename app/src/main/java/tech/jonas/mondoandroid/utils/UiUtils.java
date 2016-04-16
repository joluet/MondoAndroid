package tech.jonas.mondoandroid.utils;

import android.support.v4.util.Pair;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class UiUtils {

    @SuppressWarnings("unchecked")
    public static Pair<View, String>[] toPairs(View[] views) {
        final List<Pair<View, String>> pairs = new ArrayList<>();
        for (View view : views) {
            pairs.add(new Pair<>(view, view.getTransitionName()));
        }
        return pairs.<Pair<View, String>>toArray(new Pair[pairs.size()]);
    }
}
