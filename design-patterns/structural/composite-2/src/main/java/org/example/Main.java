package org.example;

import org.example.file.File;
import org.example.file.Folder;

public class Main {
    public static void main(String[] args) {

        Folder baseFolder = new Folder("Movies");

        baseFolder.addChild(new File("Gadar"));

        Folder comedyFolder = new Folder("Comedy-Folder");
        comedyFolder.addChild(new File("Humgama"));
        comedyFolder.addChild(new File("Dhol"));

        baseFolder.addChild(comedyFolder);

        baseFolder.addChild(new File("James Bond"));

        baseFolder.ls();
    }
}