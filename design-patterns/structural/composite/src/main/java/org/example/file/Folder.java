package org.example.file;

import java.util.ArrayList;
import java.util.List;

public class Folder implements FileSystem {

    private String folderName;

    private List<FileSystem> childList;

    public Folder(String folderName) {
        this.folderName = folderName;
        this.childList = new ArrayList<>();
    }

    public void addChild(FileSystem fileSystem) {
        childList.add(fileSystem);
    }

    @Override
    public void ls() {
        System.out.println("Folder name is " + folderName);
        for (FileSystem child : childList) {
            child.ls();
        }
    }
}
