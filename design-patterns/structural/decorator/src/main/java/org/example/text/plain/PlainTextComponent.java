package org.example.text.plain;

public class PlainTextComponent implements TextComponent {

    private String text;

    public PlainTextComponent(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
