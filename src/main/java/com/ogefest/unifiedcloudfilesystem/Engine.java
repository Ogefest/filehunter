package com.ogefest.unifiedcloudfilesystem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

abstract public class Engine {

    protected EngineConfiguration engineConfiguration;

    public Engine(EngineConfiguration conf) {
        this.engineConfiguration = conf;

        initialize();
    }

    public void initialize() {

    }

    public void finish() throws IOException {

    }

    protected EngineConfiguration getConfiguration() {
        return engineConfiguration;
    }

    abstract public EngineItem set(EngineItem engineItem, InputStream input) throws IOException;

    abstract public InputStream get(EngineItem engineItem) throws IOException;

    abstract public ArrayList<EngineItem> list(EngineItem engineItem) throws IOException, ResourceAccessException;

    abstract public boolean exists(EngineItem engineItem) throws IOException;

    abstract public void delete(EngineItem engineItem) throws IOException, ResourceAccessException;

    abstract public void move(EngineItem from, EngineItem to) throws IOException;

    abstract public void mkdir(EngineItem item) throws IOException;

}
