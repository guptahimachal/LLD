package org.example;

import org.example.text.decorator.BoldTextDecorator;
import org.example.text.decorator.ItalicTextDecorator;
import org.example.text.decorator.TextDecorator;
import org.example.text.decorator.UnderlineTextDecorator;
import org.example.text.plain.PlainTextComponent;
import org.example.text.plain.TextComponent;
import org.w3c.dom.Text;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        TextComponent plainTextComponent = new PlainTextComponent("my text");


        TextComponent boldTextComponent = new BoldTextDecorator(plainTextComponent);
        TextComponent italicTextComponent = new ItalicTextDecorator(plainTextComponent);
        TextComponent underLineTextComponent = new UnderlineTextDecorator(plainTextComponent);


        TextComponent boldItalicUnderlineTextComponent = new BoldTextDecorator(new ItalicTextDecorator(new UnderlineTextDecorator(plainTextComponent)));




        System.out.println(plainTextComponent.getText());
        System.out.println(boldTextComponent.getText());
        System.out.println(italicTextComponent.getText());
        System.out.println(underLineTextComponent.getText());
        System.out.println(boldItalicUnderlineTextComponent.getText());



    }
}