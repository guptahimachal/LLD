package org.example.text.decorator;

import org.example.text.plain.TextComponent;

public class ItalicTextDecorator extends TextDecorator {


    public ItalicTextDecorator(TextComponent textComponent) {
        super(textComponent);
    }

    @Override
    public String getText() {
        return "<I>" + super.getText() + "</I>";
    }

}
