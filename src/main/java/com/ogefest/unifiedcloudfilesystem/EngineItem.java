package com.ogefest.unifiedcloudfilesystem;

import java.io.File;
import java.time.LocalDateTime;

public class EngineItem {

    protected String name;
    protected String path;
    protected EngineItemAttribute attributes;

    public EngineItem(String path) {
        this.path = pathCleanup(path);
        updateName();

        attributes = new EngineItemAttribute();
    }

    public EngineItem(String path, EngineItemAttribute attributes) {
        this.path = pathCleanup(path);
        updateName();
        this.attributes = attributes;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return attributes.size;
    }

    public boolean isDirectory() {
        return attributes.isDirectory;
    }

    public boolean isFile() {
        return attributes.isFile;
    }

    public LocalDateTime getLastModified() {
        return attributes.lastModified;
    }

    public LocalDateTime getCreatedAt() {
        return attributes.creationTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = pathCleanup(path);
        updateName();
    }

    public EngineItem getParent() {
        String[] elems = path.split("/");
        String parentPath = "";
        int counter = 0;
        for (String e : elems) {
            if (e.equals("")) {
                continue;
            }
            if (counter == elems.length - 2) {
                break;
            }

            parentPath = parentPath + "/" + e;
            counter++;
        }

        EngineItemAttribute eia = new EngineItemAttribute();
        eia.isDirectory = true;

        return new EngineItem(parentPath, eia);
    }

    public EngineItemAttribute getAttributes() {
        return attributes;
    }

    private void updateName() {

        File f = new File(path);
        name = f.getName();

    }

    private String pathCleanup(String path) {
        if (path == null) {
            path = "/";
        }

        path = path.trim();
        if (path.equals("")) {
            path = "/";
        }
        if (!path.substring(0, 1).equals("/")) {
            path = "/" + path;
        }
        path = path.trim().replaceAll("/+", "/");
        if (path.length() > 1) {
            char lastChar = path.charAt(path.length() - 1);
            if (lastChar == '/') {
                path = path.substring(0, path.length() - 1);
            }
        }

        return path;
    }
}
