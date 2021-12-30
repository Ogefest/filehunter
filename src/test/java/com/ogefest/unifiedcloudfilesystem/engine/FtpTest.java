package com.ogefest.unifiedcloudfilesystem.engine;

import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.EngineTest;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.HashMap;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FtpTest extends EngineTest {

    @BeforeEach
    void setUp() throws IOException {

        loadConfiguration();

        HashMap<String, String> hs = new HashMap<>();
        hs.put("host", props.getProperty("ftp.host"));
        hs.put("port", props.getProperty("ftp.port"));
        hs.put("username", props.getProperty("ftp.username"));
        hs.put("password", props.getProperty("ftp.password"));
        hs.put("path", props.getProperty("ftp.path"));

        fs = new Ftp(new EngineConfiguration(hs));
    }

    @AfterAll
    void cleanup() {

    }
}