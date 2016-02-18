package tech.jonas.mondoandroid.data;

import android.content.Context;

import dagger.ObjectGraph;

public final class Injector {
    private static final String INJECTOR_SERVICE = "tech.jonas.mondoandroid.injector";

    @SuppressWarnings({"ResourceType", "WrongConstant"}) // Explicitly doing a custom service.
    public static ObjectGraph obtain(Context context) {
        return (ObjectGraph) context.getSystemService(INJECTOR_SERVICE);
    }

    public static boolean matchesService(String name) {
        return INJECTOR_SERVICE.equals(name);
    }

    private Injector() {
        throw new AssertionError("No instances.");
    }
}