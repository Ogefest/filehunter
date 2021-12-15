package com.ogefest.unifiedcloudfilesystem;

public class FileObject {

    private final EngineItem engineItem;
    private final String engineName;

    public FileObject(String engineName, EngineItem engineItem) {
        this.engineItem = engineItem;
        this.engineName = engineName;
    }

    public String getEngineName() {
        return engineName;
    }

    public EngineItem getEngineItem() {
        return engineItem;
    }
}
