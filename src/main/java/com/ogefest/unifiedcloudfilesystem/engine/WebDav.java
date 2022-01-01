package com.ogefest.unifiedcloudfilesystem.engine;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.impl.SardineException;
import com.ogefest.unifiedcloudfilesystem.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class WebDav extends Engine {

    private final Sardine webdavClient;

    private String url;
    private String username;
    private String password;

    public WebDav(EngineConfiguration conf) {
        super(conf);

        try {
            url = getConfiguration().getStringValue("url");
            username = getConfiguration().getStringValue("username");
            password = getConfiguration().getStringValue("password");
        } catch (MissingConfigurationKeyException e) {
            e.printStackTrace();
        }

        webdavClient = SardineFactory.begin(username, password);
    }

    private String getFullUrl(EngineItem item) {
        try {
            String result = url + URLEncoder.encode(item.getPath(), StandardCharsets.UTF_8.toString());
            result = url + item.getPath().replace(" ", "%20");
            return result;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Override
    public EngineItem set(EngineItem engineItem, InputStream input) throws IOException {

        if (!exists(engineItem.getParent())) {
            mkdir(engineItem.getParent());
        }

        webdavClient.put(getFullUrl(engineItem), input);

        return engineItem;
    }

    @Override
    public InputStream get(EngineItem engineItem) throws IOException {
        return webdavClient.get(getFullUrl(engineItem));
    }

    @Override
    public ArrayList<EngineItem> list(EngineItem engineItem) throws IOException, ResourceAccessException {
        ArrayList<EngineItem> result = new ArrayList<>();

        List<DavResource> davList;
        try {
            davList = webdavClient.list(getFullUrl(engineItem), 1);
        } catch (SardineException e) {
            throw new ResourceAccessException(e.getMessage());
        }

        URL baseUrl = new URL(url);

        for (DavResource res : davList) {

//            String path = baseUrl.getPath().substring(0, baseUrl.getPath().length() - 1);
//            String filePath = res.getPath().replace(path, "");

            String filePath = res.getPath().substring(baseUrl.getPath().length());

            if (filePath.equals("/")) {
                continue;
            }

//            EngineItem itemToAdd = new EngineItem(filePath);

            if (/*!itemToAdd.getPath().equals(engineItem.getPath())*/ !engineItem.getPath().equals(filePath)) {

                EngineItemAttribute attribute = new EngineItemAttribute();
                attribute.isFile = !res.isDirectory();
                attribute.isDirectory = res.isDirectory();
                attribute.size = res.getContentLength().longValue();
                attribute.lastModified = LocalDateTime.ofInstant(res.getModified().toInstant(), ZoneId.systemDefault());

                result.add(new EngineItem(filePath, attribute));
            }
        }

        return result;
    }

    @Override
    public boolean exists(EngineItem engineItem) throws IOException {
        boolean result = webdavClient.exists(getFullUrl(engineItem));
        /*
        exists() works only for files, we also should check if directory exists
         */
        if (!result) {
            try {
                ArrayList<EngineItem> tmpList = list(engineItem);
            } catch (ResourceAccessException e) {
                return false;
            }
            return true;
        }

        return true;
    }

    @Override
    public void delete(EngineItem engineItem) throws IOException {
        webdavClient.delete(getFullUrl(engineItem));
    }

    @Override
    public void move(EngineItem from, EngineItem to) throws IOException {
        webdavClient.move(getFullUrl(from), getFullUrl(to));
    }

    @Override
    public void mkdir(EngineItem item) throws IOException {
        EngineItem parentItem = item.getParent();
        if (!parentItem.getPath().equals("") && !exists(parentItem)) {
            mkdir(parentItem);
        }
        webdavClient.createDirectory(getFullUrl(item));
    }
}
