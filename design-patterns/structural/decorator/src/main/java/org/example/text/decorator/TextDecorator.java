package org.example.text.decorator;

import org.example.text.plain.TextComponent;

public abstract class TextDecorator implements TextComponent {

    private TextComponent textComponent;

    public TextDecorator(TextComponent textComponent) {
        this.textComponent = textComponent;
    }

    @Override
    public String getText() {
        return textComponent.getText();
    }
}
