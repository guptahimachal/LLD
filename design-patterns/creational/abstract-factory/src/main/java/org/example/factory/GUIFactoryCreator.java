package org.example.factory;

public class GUIFactoryCreator {
    public static GUIFactory createFactory() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return new WindowsFactory();
        } else if (osName.contains("mac")) {
            return new MacFactory();
        } else {
            throw new UnsupportedOperationException("Unsupported OS: " + osName);
        }
    }
}
