package com.ogefest.unifiedcloudfilesystem.engine;

import com.ogefest.unifiedcloudfilesystem.*;

import java.io.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

public class FileSystem extends Engine {

    public FileSystem(EngineConfiguration c) {
        super(c);
    }

    @Override
    public EngineItem set(EngineItem engineItem, InputStream input) {

        FileOutputStream fout = null;
        try {
            String outputPath = getFullPath(engineItem);
            File parent = new File(engineItem.getPath()).getParentFile();
            if (parent != null && !parent.exists()) {
                EngineItemAttribute eia = new EngineItemAttribute();
                eia.isDirectory = true;
                mkdir(new EngineItem(parent.getPath(), eia));
            }

            File f = new File(outputPath);


            fout = new FileOutputStream(outputPath);

            byte[] buffer = new byte[10 * 1024 * 1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                fout.write(buffer, 0, bytesRead);
            }
            fout.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new EngineItem(engineItem.getPath(), engineItem.getAttributes());
    }

    @Override
    public InputStream get(EngineItem engineItem) {
        try {
            return new FileInputStream(getFullPath(engineItem));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<EngineItem> list(EngineItem engineItem) {
        File d = new File(getFullPath(engineItem));

        File[] files = d.listFiles();
        ArrayList<EngineItem> result = new ArrayList<>();
        if (files != null) {
            for (File f : files) {

                EngineItemAttribute attribute = new EngineItemAttribute();
                attribute.isFile = f.isFile();
                attribute.isDirectory = f.isDirectory();
                attribute.size = f.length();
                attribute.lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());

                EngineItem tmp = new EngineItem(engineItem.getPath() + "/" + f.getName(), attribute);
                result.add(tmp);
            }
        }

        return result;
    }

    @Override
    public boolean exists(EngineItem engineItem) {
        File f = new File(getFullPath(engineItem));

        return f.exists();
    }

    @Override
    public void delete(EngineItem engineItem) throws IOException, ResourceAccessException {
        File f = new File(getFullPath(engineItem));
        ArrayList<EngineItem> contentInside = list(engineItem);
        if (contentInside.size() > 0) {
            for (EngineItem ei : contentInside) {
                delete(ei);
            }
        }

        f.delete();
    }

    @Override
    public void move(EngineItem from, EngineItem to) {
        File f = new File(getFullPath(from));
        f.renameTo(new File(getFullPath(to)));
    }

    @Override
    public void mkdir(EngineItem item) throws IOException {
        File f = new File(getFullPath(item));
        EngineItem parent = item.getParent();
        if (!exists(parent)) {
            mkdir(parent);
        }
        f.mkdir();
    }

    private String getFullPath(EngineItem item) {
        try {
            return getConfiguration().getStringValue("path") + File.separator + item.getPath();
        } catch (MissingConfigurationKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

}
