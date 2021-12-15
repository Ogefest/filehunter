package com.ogefest.unifiedcloudfilesystem;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class UnifiedCloudFileSystem {

    private final HashMap<String, Engine> engineRegistry = new HashMap<>();

    public void registerEngine(String name, Engine engine) {
        if (engineRegistry.containsKey(name)) {
            //
        }
        engineRegistry.put(name, engine);
    }

    public void unregisterEngine(String name) throws IOException {
        engineRegistry.get(name).finish();
        engineRegistry.remove(name);
    }

    public Engine getEngine(String name) {
        return engineRegistry.get(name);
    }

    public ArrayList<FileObject> list(FileObject engineItem) throws IOException, ResourceAccessException {

        ArrayList<EngineItem> eiList = engineRegistry.get(engineItem.getEngineName()).list(engineItem.getEngineItem());
        ArrayList<FileObject> fileObjects = new ArrayList<>();
        for (EngineItem ei : eiList) {
            fileObjects.add(new FileObject(engineItem.getEngineName(), ei));
        }

        return fileObjects;
    }

    public void write(FileObject engineItem, File file) throws IOException {
        try {
            InputStream input = new FileInputStream(file);
            write(engineItem, input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void write(FileObject engineItem, byte[] data) throws IOException {
        InputStream input = new ByteArrayInputStream(data);
        write(engineItem, input);
    }

    public void write(FileObject engineItem, InputStream input) throws IOException {
        Engine ei = engineRegistry.get(engineItem.getEngineName());
        ei.set(engineItem.getEngineItem(), input);
    }

    public InputStream read(FileObject item) throws IOException {
        return engineRegistry.get(item.getEngineName()).get(item.getEngineItem());
    }

    public void copy(FileObject from, FileObject to) throws IOException {
        Engine engineFrom = engineRegistry.get(from.getEngineName());
        Engine engineTo = engineRegistry.get(to.getEngineName());

        if (from.getEngineName().equals(to.getEngineName())) {
            engineFrom.move(from.getEngineItem(), to.getEngineItem());
        } else {
            InputStream is = engineFrom.get(from.getEngineItem());
            engineTo.set(to.getEngineItem(), is);
        }
    }

    public void delete(FileObject engineItem) throws IOException, ResourceAccessException {
        engineRegistry.get(engineItem.getEngineName()).delete(engineItem.getEngineItem());
    }

    public boolean exists(FileObject item) throws IOException {
        return engineRegistry.get(item.getEngineName()).exists(item.getEngineItem());
    }

    public void move(FileObject from, FileObject to) throws IOException, ResourceAccessException {
        Engine engineFrom = engineRegistry.get(from.getEngineName());
        Engine engineTo = engineRegistry.get(to.getEngineName());

        if (from.getEngineName().equals(to.getEngineName())) {
            engineFrom.move(from.getEngineItem(), to.getEngineItem());
        } else {
            InputStream is = engineFrom.get(from.getEngineItem());
            engineTo.set(to.getEngineItem(), is);
            engineFrom.delete(from.getEngineItem());
        }

    }

    public FileObject getByPath(String engineName, String path) {
        EngineItemAttribute eia = new EngineItemAttribute();
        eia.isDirectory = true;

        return new FileObject(engineName, new EngineItem(path, eia));
    }

}
