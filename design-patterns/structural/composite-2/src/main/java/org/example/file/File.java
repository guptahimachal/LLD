package org.example.file;

public class File extends FileSystem {

    private final String fileName;

    public File(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void ls(int level) {

        for (int i=0;i<level;i++) {
            System.out.print("   ");
        }
        System.out.println("-" + fileName);

    }
}
