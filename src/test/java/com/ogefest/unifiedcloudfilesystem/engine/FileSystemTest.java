package com.ogefest.unifiedcloudfilesystem.engine;

import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.EngineTest;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FileSystemTest extends EngineTest {

    protected File temporaryDirectory;

    @BeforeAll
    void setUp() throws IOException {

        Path tempDirWithPrefix = Files.createTempDirectory("ucfstestdir");
        temporaryDirectory = tempDirWithPrefix.toFile();
        if (!temporaryDirectory.exists()) {
            temporaryDirectory.mkdirs();
        }

        HashMap<String, String> hs = new HashMap<>();
        hs.put("path", temporaryDirectory.getAbsolutePath());

        fs = new FileSystem(new EngineConfiguration(hs));
    }

    @AfterAll
    void cleanup() {
        Path pathToBeDeleted = Paths.get(temporaryDirectory.getPath());

        try {
            Files.walk(pathToBeDeleted)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}