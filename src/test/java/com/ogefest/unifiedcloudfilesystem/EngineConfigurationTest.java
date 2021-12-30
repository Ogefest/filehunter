package com.ogefest.unifiedcloudfilesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EngineConfigurationTest {

    EngineConfiguration engineConfiguration;

    @BeforeEach
    void setUp() {
        HashMap<String, String> input = new HashMap<>();
        input.put("str1", "str");
        input.put("int1", "123");
        input.put("boolean", "false");

        engineConfiguration = new EngineConfiguration(input);
    }

    @Test
    void checkStringTest() throws MissingConfigurationKeyException, InvalidConfigurationValueException {
        assertTrue(engineConfiguration.getStringValue("str1").equals("str"));
    }

    @Test
    void checkIntTest() throws MissingConfigurationKeyException {
        assertTrue(engineConfiguration.getIntValue("int1") == 123);
    }

    @Test
    void checkBooleanTest() throws InvalidConfigurationValueException, MissingConfigurationKeyException {
        assertTrue(engineConfiguration.getBooleanValue("boolean") == false);
    }

    @Test
    void checkMissingKey() {
        assertThrows(MissingConfigurationKeyException.class, () -> engineConfiguration.getStringValue("missing"));
        assertThrows(MissingConfigurationKeyException.class, () -> engineConfiguration.getIntValue("missing"));
        assertThrows(MissingConfigurationKeyException.class, () -> engineConfiguration.getBooleanValue("missing"));
        assertThrows(InvalidConfigurationValueException.class, () -> engineConfiguration.getBooleanValue("int1"));
    }

}
