package com.ogefest.unifiedcloudfilesystem.engine;

import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.EngineTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.util.HashMap;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebDavTest extends EngineTest {

    @BeforeEach
    void setUp() throws IOException {

        loadConfiguration();

        HashMap<String, String> hs = new HashMap<>();
        hs.put("username", props.getProperty("webdav.username"));
        hs.put("password", props.getProperty("webdav.password"));
        hs.put("url", props.getProperty("webdav.url"));

        fs = new WebDav(new EngineConfiguration(hs));
    }
}