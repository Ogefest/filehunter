package com.ogefest.unifiedcloudfilesystem;

import com.ogefest.unifiedcloudfilesystem.engine.FileSystem;
import org.junit.jupiter.api.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UnifiedCloudFileSystemTest {

    UnifiedCloudFileSystem ucfs;

    Engine engine1;
    Engine engine2;

    File tmp1;
    File tmp2;

    @BeforeAll
    void setup() throws IOException {
        Path tempDirWithPrefix = Files.createTempDirectory("ucfstestdir");
        tmp1 = tempDirWithPrefix.toFile();
        if (!tmp1.exists()) {
            tmp1.mkdirs();
        }

        Path tempDirWithPrefix2 = Files.createTempDirectory("ucfstestdir");
        tmp2 = tempDirWithPrefix2.toFile();
        if (!tmp2.exists()) {
            tmp2.mkdirs();
        }

        HashMap<String, String> hs = new HashMap<>();
        hs.put("path", tmp1.getAbsolutePath());

        engine1 = new FileSystem(new EngineConfiguration(hs));

        HashMap<String, String> hs2 = new HashMap<>();
        hs2.put("path", tmp2.getAbsolutePath());
        engine2 = new FileSystem(new EngineConfiguration(hs2));

    }

    @AfterAll
    void cleanup() {
        Path pathToBeDeleted1 = Paths.get(tmp1.getPath());
        Path pathToBeDeleted2 = Paths.get(tmp2.getPath());

        try {
            Files.walk(pathToBeDeleted1)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);

            Files.walk(pathToBeDeleted2)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(1)
    void registerEngine() {
        ucfs = new UnifiedCloudFileSystem();
        assertNull(ucfs.getEngine("e1"));
        ucfs.registerEngine("e1", engine1);
        assertNotNull(ucfs.getEngine("e1"));
    }

    @Test
    @Order(2)
    void unregisterEngine() throws IOException {
        ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine("e1", engine1);
        assertNotNull(ucfs.getEngine("e1"));
        ucfs.unregisterEngine("e1");
        assertNull(ucfs.getEngine("e1"));
    }

    @Test
    @Order(3)
    void getEngine() {
        ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine("e1", engine1);

        assertTrue(ucfs.getEngine("e1").equals(engine1));
    }

    @Test
    @Order(5)
    void list() throws IOException, ResourceAccessException {
        FileObject fo = ucfs.getByPath("e1", "/");
        ArrayList<FileObject> objectList = ucfs.list(fo);
        assertTrue(objectList.get(0).getEngineItem().getName().equals("asd.txt"));

        FileObject fo2 = ucfs.getByPath("e2", "/");
        assertTrue(ucfs.list(fo2).size() == 0);
    }

    @Test
    @Order(4)
    void write() throws IOException {
        ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine("e1", engine1);
        ucfs.registerEngine("e2", engine2);

        FileObject fo = ucfs.getByPath("e1", "/asd.txt");

        assertFalse(ucfs.exists(fo));
        byte[] initialArray = {'1', '2', '3'};
        InputStream input = new ByteArrayInputStream(initialArray);
        ucfs.write(fo, input);
        input.close();

        assertTrue(ucfs.exists(fo));

        File check = new File(tmp1.getAbsolutePath() + "/asd.txt");
        assertTrue(check.length() == 3);
    }

    @Test
    void testSet() {
    }

    @Test
    void testSet1() {
    }

    @Test
    @Order(6)
    void copy() throws IOException {
        FileObject fin = ucfs.getByPath("e1", "/asd.txt");
        FileObject fout = ucfs.getByPath("e2", "/cde.txt");

        assertFalse(ucfs.exists(fout));
        ucfs.copy(fin, fout);
        assertTrue(ucfs.exists(fout));
    }

    @Test
    @Order(9)
    void delete() throws IOException, ResourceAccessException {
        FileObject fo = ucfs.getByPath("e2", "/cde2.txt");
        assertTrue(ucfs.exists(fo));
        ucfs.delete(fo);
        assertFalse(ucfs.exists(fo));
    }

    @Test
    @Order(8)
    void move() throws IOException, ResourceAccessException {
        FileObject fin = ucfs.getByPath("e1", "/asd.txt");
        FileObject fout = ucfs.getByPath("e2", "/cde2.txt");

        assertFalse(ucfs.exists(fout));
        ucfs.move(fin, fout);
        assertTrue(ucfs.exists(fout));
    }

    @Test
    @Order(10)
    void read() throws IOException {
        FileObject fin = ucfs.getByPath("e2", "/cde.txt");
        InputStream is = ucfs.read(fin);
        byte[] data = is.readAllBytes();

        byte[] initialArray = {'1', '2', '3'};
        assertArrayEquals(data, initialArray);
    }

}