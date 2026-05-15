package org.example.text.decorator;

import org.example.text.plain.TextComponent;

public class BoldTextDecorator extends TextDecorator {

    public BoldTextDecorator(TextComponent textComponent) {
        super(textComponent);
    }

    @Override
    public String getText() {
        return "<b>" + super.getText() + "</b>";
    }

}
