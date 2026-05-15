package org.example.base;

public class MacButton implements Button {
    @Override
    public void paint() {
        System.out.println("Rendering a button in Mac style");
    }
}
