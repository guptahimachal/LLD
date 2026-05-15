package org.example.factory;

import org.example.base.Button;
import org.example.base.Checkbox;

public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
