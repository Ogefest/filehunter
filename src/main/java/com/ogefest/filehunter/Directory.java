package com.ogefest.filehunter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Directory implements Serializable {
    private String name;
    private ArrayList<String> path = new ArrayList<>();
    private ArrayList<String> ignorePath = new ArrayList<>();
    private ArrayList<String> ignorePhrase = new ArrayList<>();
    private ArrayList<String> ignoreExtension = new ArrayList<>();
    private int maxDepth = 20;
    private String indexMode = "full";
    private int hashSize = 0;
    private int intervalUpdateStructure = 3600;
    private int intervalUpdateMetadata = 7200;
    private boolean extractMetadata = true;
    private LocalDateTime lastStructureIndexed;
    private LocalDateTime lastMetadataIndexed;

    public ArrayList<String> getPath() {
        return path;
    }

    public void setPath(ArrayList<String> path) {
        this.path = path;
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

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }

    public String getIndexMode() {
        return indexMode;
    }

    public void setIndexMode(String indexMode) {
        this.indexMode = indexMode;
    }

    public int getHashSize() {
        return hashSize;
    }

    public void setHashSize(int hashSize) {
        this.hashSize = hashSize;
    }

    public int getIntervalUpdateStructure() {
        return intervalUpdateStructure;
    }

    public void setIntervalUpdateStructure(int intervalUpdateStructure) {
        this.intervalUpdateStructure = intervalUpdateStructure;
    }

    public int getIntervalUpdateMetadata() {
        return intervalUpdateMetadata;
    }

    public void setIntervalUpdateMetadata(int intervalUpdateMetadata) {
        this.intervalUpdateMetadata = intervalUpdateMetadata;
    }

    public boolean isExtractMetadata() {
        return extractMetadata;
    }

    public void setExtractMetadata(boolean extractMetadata) {
        this.extractMetadata = extractMetadata;
    }

    public LocalDateTime getLastStructureIndexed() {
        return lastStructureIndexed;
    }

    public void setLastStructureIndexed(LocalDateTime lastStructureIndexed) {
        this.lastStructureIndexed = lastStructureIndexed;
    }

    public LocalDateTime getLastMetadataIndexed() {
        return lastMetadataIndexed;
    }

    public void setLastMetadataIndexed(LocalDateTime lastMetadataIndexed) {
        this.lastMetadataIndexed = lastMetadataIndexed;
    }

    public Directory() {

    }

}
