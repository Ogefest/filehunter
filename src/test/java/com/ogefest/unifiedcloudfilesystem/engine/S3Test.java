package com.ogefest.unifiedcloudfilesystem.engine;

import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.EngineTest;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.util.HashMap;

class S3Test extends EngineTest {
    @BeforeAll
    void setUp() throws IOException {

        loadConfiguration();

        HashMap<String, String> hs = new HashMap<>();
        hs.put("endpoint", props.getProperty("s3.endpoint"));
        hs.put("region", props.getProperty("s3.region"));
        hs.put("bucket", props.getProperty("s3.bucket"));
        hs.put("accesskey", props.getProperty("s3.accesskey"));
        hs.put("secretkey", props.getProperty("s3.secretkey"));

        fs = new S3(new EngineConfiguration(hs));
    }
}