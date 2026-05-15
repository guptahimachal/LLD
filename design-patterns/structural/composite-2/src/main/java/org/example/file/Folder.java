package org.example.file;

import java.util.ArrayList;
import java.util.List;

public class Folder extends FileSystem {

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
    protected void ls(int level) {

        for(int i=0;i<level;i++) {
            System.out.print("   ");
        }

        System.out.println("/" + folderName);

        for(FileSystem child : childList) {
            child.ls(level + 1);
        }

    }
}
