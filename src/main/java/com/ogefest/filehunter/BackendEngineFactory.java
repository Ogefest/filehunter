package com.ogefest.filehunter;

import com.ogefest.unifiedcloudfilesystem.Engine;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.engine.*;

import java.io.IOException;

public class BackendEngineFactory {

    public static Engine get(String name, EngineConfiguration ec) {

        if (name.equals("filesystem")) {
            return new FileSystem(ec);
        }
        if (name.equals("s3")) {
            return new S3(ec);
        }
        if (name.equals("webdav")) {
            return new WebDav(ec);
        }
        if (name.equals("ftp")) {
            try {
                return new Ftp(ec);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (name.equals("nfs")) {
            return new Nfs(ec);
        }

        return null;
    }
}
