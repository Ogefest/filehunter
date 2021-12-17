package com.ogefest.filehunter;

public class FileItem {

    private String name;
    private String path;
    private String index;
    private int size;
    private String ext;
    private String type;

    public FileItem(FileInfo fi) {
        index = fi.getIndexName();
        path = fi.getPath();
        name = fi.getName();
        ext = fi.getExt();

        type = "f";
        if (fi.isDirectory()) {
            type = "d";
        }
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
