package com.ogefest.unifiedcloudfilesystem.engine;

import com.emc.ecs.nfsclient.nfs.NfsSetAttributes;
import com.emc.ecs.nfsclient.nfs.NfsTime;
import com.emc.ecs.nfsclient.nfs.io.Nfs3File;
import com.emc.ecs.nfsclient.nfs.io.NfsFile;
import com.emc.ecs.nfsclient.nfs.io.NfsFileInputStream;
import com.emc.ecs.nfsclient.nfs.io.NfsFileOutputStream;
import com.emc.ecs.nfsclient.nfs.nfs3.Nfs3;
import com.emc.ecs.nfsclient.rpc.CredentialUnix;
import com.ogefest.unifiedcloudfilesystem.*;
import com.sun.security.auth.module.UnixSystem;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Nfs extends Engine {

    private EngineConfiguration conf;
    private Nfs3 nfs;

    public Nfs(EngineConfiguration conf) {
        super(conf);

        try {
            nfs = new Nfs3(conf.getStringValue("host") +":"+conf.getStringValue("path"), new CredentialUnix(0, 0, null), 3);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MissingConfigurationKeyException e) {
            e.printStackTrace();
        }
    }

    private Nfs3 getNfs() {
        try {
            int uid = (int) new UnixSystem().getUid();
            int gid = (int) new UnixSystem().getGid();
            Nfs3 result = new Nfs3(getConfiguration().getStringValue("host") +":"+getConfiguration().getStringValue("path"),
                    new CredentialUnix(uid, gid, null),
                    3);
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MissingConfigurationKeyException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public EngineItem set(EngineItem engineItem, InputStream input) throws IOException {

        Nfs3File fileOut = new Nfs3File(getNfs(), engineItem.getPath());

        if (!fileOut.getParentFile().exists()) {
            mkdir(engineItem.getParent());
        }

        NfsFileOutputStream outputStream = new NfsFileOutputStream(fileOut);
        input.transferTo(outputStream);
        outputStream.close();
        fileOut.setMode(NfsFile.ownerReadModeBit | NfsFile.ownerWriteModeBit | NfsFile.groupReadModeBit | NfsFile.groupWriteModeBit | NfsFile.othersWriteModeBit | NfsFile.othersReadModeBit);

        return list(engineItem).get(0);
    }

    @Override
    public InputStream get(EngineItem engineItem) throws IOException {

        Nfs3File file = new Nfs3File(getNfs(), engineItem.getPath());
        NfsFileInputStream inputStream = new NfsFileInputStream(file);

        return inputStream;
    }

    @Override
    public ArrayList<EngineItem> list(EngineItem engineItem) throws IOException {
        ArrayList<EngineItem> result = new ArrayList<>();

        Nfs3File file = new Nfs3File(getNfs(), engineItem.getPath());
        if (!file.isDirectory()) {
            if (file.exists()) {
                EngineItemAttribute attribute = new EngineItemAttribute();
                attribute.isFile = file.isFile();
                attribute.isDirectory = file.isDirectory();
                attribute.size = file.length();
                attribute.lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(file.lastModified()), ZoneId.systemDefault());

                EngineItem tmp = new EngineItem(engineItem.getPath() + "/" + file.getName(), attribute);
                result.add(tmp);
            }
            return result;
        }
        List<Nfs3File> fileList = file.listFiles();


        for (Nfs3File f : fileList) {

            EngineItemAttribute attribute = new EngineItemAttribute();
            attribute.isFile = f.isFile();
            attribute.isDirectory = f.isDirectory();
            attribute.size = f.length();
            attribute.lastModified = LocalDateTime.ofInstant(Instant.ofEpochMilli(f.lastModified()), ZoneId.systemDefault());

            EngineItem tmp = new EngineItem(engineItem.getPath() + "/" + f.getName(), attribute);
            result.add(tmp);

        }

        return result;
    }

    @Override
    public boolean exists(EngineItem engineItem) throws IOException {
        Nfs3File file = new Nfs3File(getNfs(), engineItem.getPath());

        return file.exists();
    }

    @Override
    public void delete(EngineItem engineItem) throws IOException, ResourceAccessException {
        Nfs3File file = new Nfs3File(getNfs(), engineItem.getPath());
        if (file.isFile()) {
            file.delete();
        } else {
            List<Nfs3File> children = file.listFiles();
            for (Nfs3File f : children) {
                delete(new EngineItem(f.getPath()));
            }
            file.delete();
        }
    }

    @Override
    public void move(EngineItem from, EngineItem to) throws IOException {
        Nfs3File fileFrom = new Nfs3File(getNfs(), from.getPath());
        Nfs3File fileTo = new Nfs3File(getNfs(), to.getPath());

        fileFrom.renameTo(fileTo);
    }

    @Override
    public void mkdir(EngineItem item) throws IOException {

        ArrayList<EngineItem> itemsToCreate = new ArrayList<>();
        itemsToCreate.add(item);
        EngineItem tmp = item;
        do {

            tmp = tmp.getParent();
            if (tmp.getPath().equals("/")) {
                break;
            }
            itemsToCreate.add(tmp);

        } while(true);

        for (int i = itemsToCreate.size() -1; i >= 0; i--) {
            Nfs3File file = new Nfs3File(getNfs(), itemsToCreate.get(i).getPath());

            if (!file.exists()) {
                file.mkdir();
                file.setMode(NfsFile.ownerReadModeBit | NfsFile.ownerWriteModeBit | NfsFile.ownerExecuteModeBit |
                        NfsFile.groupReadModeBit | NfsFile.groupExecuteModeBit | NfsFile.groupWriteModeBit |
                        NfsFile.othersReadModeBit | NfsFile.othersWriteModeBit | NfsFile.othersExecuteModeBit);
            }
        }

    }
}
