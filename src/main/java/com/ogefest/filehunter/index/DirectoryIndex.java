package com.ogefest.filehunter.index;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class DirectoryIndex {
    private String name;
    private ArrayList<String> ignorePath = new ArrayList<>();
    private ArrayList<String> ignorePhrase = new ArrayList<>();
    private ArrayList<String> ignoreExtension = new ArrayList<>();
    private int intervalUpdateStructure = 3600;
    private ReindexType reindexType = ReindexType.RECURRING;
    private boolean extractMetadata = false;
    private String lastStructureIndexed;
    private String lastMetadataIndexed;
    private String type;
    private HashMap<String, String> configuration = new HashMap<>();
    private StatusType status = StatusType.UNKNOWN;
    private int reindexSessionId = 0;

    public DirectoryIndex() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(HashMap<String, String> configuration) {
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getIgnorePath() {
        return ignorePath;
    }

    public void setIgnorePath(ArrayList<String> ignorePath) {
        this.ignorePath = ignorePath;
    }

    public ArrayList<String> getIgnorePhrase() {
        return ignorePhrase;
    }

    public void setIgnorePhrase(ArrayList<String> ignorePhrase) {
        this.ignorePhrase = ignorePhrase;
    }

    public ArrayList<String> getIgnoreExtension() {
        return ignoreExtension;
    }

    public void setIgnoreExtension(ArrayList<String> ignoreExtension) {
        this.ignoreExtension = ignoreExtension;
    }

    public int getIntervalUpdateStructure() {
        return intervalUpdateStructure;
    }

    public void setIntervalUpdateStructure(int intervalUpdateStructure) {
        this.intervalUpdateStructure = intervalUpdateStructure;
    }

    public boolean isExtractMetadata() {
        return extractMetadata;
    }

    public void setExtractMetadata(boolean extractMetadata) {
        this.extractMetadata = extractMetadata;
    }

    public LocalDateTime getLastStructureIndexed() {
        if (lastStructureIndexed == null) {
            return LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        return LocalDateTime.parse(lastStructureIndexed, DateTimeFormatter.ISO_DATE_TIME);
//        return lastStructureIndexed;
    }

    public void setLastStructureIndexed(LocalDateTime lastStructureIndexed) {
        this.lastStructureIndexed = lastStructureIndexed.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public LocalDateTime getLastMetadataIndexed() {
        if (lastMetadataIndexed == null) {
            return LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        return LocalDateTime.parse(lastMetadataIndexed, DateTimeFormatter.ISO_DATE_TIME);
//        return lastMetadataIndexed;
    }

    public void setLastMetadataIndexed(LocalDateTime lastMetadataIndexed) {
        this.lastMetadataIndexed = lastMetadataIndexed.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public StatusType getStatus() {
        return status;
    }

    public void setStatus(StatusType status) {
        this.status = status;
    }

    public ReindexType getReindexType() {
        return reindexType;
    }

    public void setReindexType(ReindexType reindexType) {
        this.reindexType = reindexType;
    }

    public int getReindexSessionId() {
        return reindexSessionId;
    }

    public void setReindexSessionId(int reindexSessionId) {
        this.reindexSessionId = reindexSessionId;
    }
}
