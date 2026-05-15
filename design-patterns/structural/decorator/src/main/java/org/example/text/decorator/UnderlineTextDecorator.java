package org.example.text.decorator;

import org.example.text.plain.TextComponent;

public class UnderlineTextDecorator extends TextDecorator {

    public UnderlineTextDecorator(TextComponent textComponent) {
        super(textComponent);
    }

    @Override
    public String getText() {
        return "<U>" + super.getText() + "</U>";
    }

}
