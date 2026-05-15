package org.example;

import org.example.base.Button;
import org.example.base.Checkbox;
import org.example.factory.GUIFactory;
import org.example.factory.GUIFactoryCreator;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    private Button button;
    private Checkbox checkbox;

    public Main(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void paint() {
        button.paint();
        checkbox.paint();
    }

    public static void main(String[] args) {
        GUIFactory factory = GUIFactoryCreator.createFactory();
        Main app = new Main(factory);
        app.paint();
    }
}