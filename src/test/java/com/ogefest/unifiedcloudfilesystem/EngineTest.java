package com.ogefest.unifiedcloudfilesystem;

import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
abstract public class EngineTest {

    protected Engine fs;
    protected Properties props;

    protected void loadConfiguration() {
        try (InputStream input = new FileInputStream("./runtime.properties")) {

            props = new Properties();
            props.load(input);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void set() throws IOException {

        EngineItem ei = new EngineItem("abc.txt");
        byte[] initialArray = {'1', '2', '3'};
        InputStream input = new ByteArrayInputStream(initialArray);
        fs.set(ei, input);
        input.close();
        assertTrue(fs.exists(ei));
    }

    @Test
    @Order(2)
    void get() throws IOException {
        EngineItem ei = new EngineItem("abc.txt");
        InputStream is = fs.get(ei);
        byte[] fdata = is.readAllBytes();

        byte[] initialArray = {'1', '2', '3'};

        assertArrayEquals(fdata, initialArray);
    }

    @Test
    @Order(4)
    void list() throws IOException, ResourceAccessException {
        ArrayList<EngineItem> lst = fs.list(new EngineItem("/"));
        assertTrue(lst.get(0).getName().equals("abc.txt"));

        EngineItem ei = lst.get(0);

        assertTrue(ei.isFile());
        assertFalse(ei.isDirectory());
        assertEquals(ei.getSize(), 3);
    }

    @Test
    @Order(3)
    void exists() throws IOException {
        assertTrue(fs.exists(new EngineItem("abc.txt")));
        assertFalse(fs.exists(new EngineItem("/d1/d2/abc.txt")));
    }

    @Test
    @Order(6)
    void delete() throws IOException, ResourceAccessException {
        EngineItem toDelete = new EngineItem("cde.txt");
        assertTrue(fs.exists(toDelete));
        fs.delete(toDelete);
        assertFalse(fs.exists(toDelete));
    }

    @Test
    @Order(5)
    void move() throws IOException {
        EngineItem from = new EngineItem("abc.txt");
        EngineItem to = new EngineItem("cde.txt");

        assertTrue(fs.exists(from));
        assertFalse(fs.exists(to));
        fs.move(from, to);
        assertTrue(fs.exists(to));
        assertFalse(fs.exists(from));
    }

    @Test
    @Order(7)
    void mkdir() throws IOException {
        EngineItem ei = new EngineItem("/dir1/dir2/dir3");
        EngineItem ei2 = new EngineItem("/dir1/dir3/dir5");

        assertFalse(fs.exists(ei));
        fs.mkdir(ei);
        assertTrue(fs.exists(ei));

        assertFalse(fs.exists(ei2));
        fs.mkdir(ei2);
        assertTrue(fs.exists(ei2));
    }

    @Test
    @Order(8)
    void saveFileInSubdirectory() throws IOException {
        EngineItem ei = new EngineItem("/filedir1/filedir2/filedir3/abc.txt");
        assertFalse(fs.exists(ei));

        byte[] initialArray = {'1', '2', '3'};
        InputStream input = new ByteArrayInputStream(initialArray);
        fs.set(ei, input);
        input.close();
        assertTrue(fs.exists(ei));
    }

    @Test
    @Order(9)
    void deleteDirectoryWithContent() throws IOException, ResourceAccessException {
        EngineItem ei = new EngineItem("/filedir1");
        assertTrue(fs.exists(ei));
        fs.delete(ei);
        assertFalse(fs.exists(ei));

        EngineItem ei2 = new EngineItem("/dir1");
        assertTrue(fs.exists(ei2));
        fs.delete(ei2);
        assertFalse(fs.exists(ei2));
    }
}