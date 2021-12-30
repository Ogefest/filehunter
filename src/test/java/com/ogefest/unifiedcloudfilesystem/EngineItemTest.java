package com.ogefest.unifiedcloudfilesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineItemTest {

    EngineItem engineItem;

    @BeforeEach
    void setUp() {
        engineItem = new EngineItem("path1/name1");
    }

    @Test
    void getName() {
        assertEquals(engineItem.getName(), "name1");
    }

    @Test
    void getPath() {
        assertEquals(engineItem.getPath(), "/path1/name1");
    }

    @Test
    void setPath() {
        engineItem.setPath("path2");
        assertEquals(engineItem.getPath(), "/path2");

        engineItem.setPath("/");
        assertEquals(engineItem.getPath(), "/");

        engineItem.setPath("p1/p2");
        assertEquals(engineItem.getPath(), "/p1/p2");
    }

    @Test
    void setName() {
        engineItem.setPath("path2/name2");
        assertEquals(engineItem.getName(), "name2");
    }

    @Test
    void pathCleanup() {
        EngineItem ei = new EngineItem("/");
        assertEquals(ei.getPath(), "/");

        ei = new EngineItem("//");
        assertEquals(ei.getPath(), "/");

        ei = new EngineItem("");
        assertEquals(ei.getPath(), "/");

        ei = new EngineItem("abc");
        assertEquals(ei.getPath(), "/abc");

        ei = new EngineItem(" ");
        assertEquals(ei.getPath(), "/");

        ei = new EngineItem(" abc");
        assertEquals(ei.getPath(), "/abc");

        ei = new EngineItem(" abc");
        assertEquals(ei.getPath(), "/abc");

        ei = new EngineItem("/abc/");
        assertEquals(ei.getPath(), "/abc");

    }
}