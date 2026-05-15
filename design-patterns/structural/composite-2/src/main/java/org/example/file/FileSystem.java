package org.example.file;

public abstract class FileSystem {

    public void ls() {
        ls(0);
    }

    protected abstract void ls(int level);
}
