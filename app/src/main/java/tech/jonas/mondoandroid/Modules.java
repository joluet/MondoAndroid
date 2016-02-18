package tech.jonas.mondoandroid;

final class Modules {
    static Object[] list(MondoApp app) {
        return new Object[]{
                new MondoModule(app)
        };
    }

    private Modules() {
        // No instances.
    }
}