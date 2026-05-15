package org.example.factory;

import org.example.base.Button;
import org.example.base.Checkbox;
import org.example.base.WindowsButton;
import org.example.base.WindowsCheckbox;
public class WindowsFactory implements GUIFactory {

    @Override
    public Button createButton() {
        return new WindowsButton();
    }

    @Override
    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}
