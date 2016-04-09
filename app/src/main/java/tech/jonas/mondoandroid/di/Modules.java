package tech.jonas.mondoandroid.di;

import tech.jonas.mondoandroid.MondoApp;

public class Modules {
    private Modules() {
        // No instances.
    }

    public static Object[] list(MondoApp app) {
        return new Object[]{
                new MondoModule(app)
        };
    }
}