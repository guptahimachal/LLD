package org.example.factory;

import org.example.base.Button;
import org.example.base.Checkbox;
import org.example.base.MacButton;
import org.example.base.MacCheckbox;

public class MacFactory implements GUIFactory {
    @Override
    public Button createButton() {
        return new MacButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}
