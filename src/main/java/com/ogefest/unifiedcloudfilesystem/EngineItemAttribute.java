package com.ogefest.unifiedcloudfilesystem;

import java.time.LocalDateTime;

public class EngineItemAttribute {
    public boolean isDirectory = false;
    public boolean isFile = false;
    public long size = 0;
    public LocalDateTime creationTime = LocalDateTime.MIN;
    public LocalDateTime lastModified = LocalDateTime.MIN;
}
