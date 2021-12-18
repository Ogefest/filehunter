package com.ogefest.unifiedcloudfilesystem.engine;

import com.ogefest.unifiedcloudfilesystem.*;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class S3 extends Engine {

    private MinioClient minioClient;
    private String bucketName;

    public S3(EngineConfiguration c) {
        super(c);


        try {
            bucketName = c.getStringValue("bucket");

            minioClient = MinioClient.builder()
                    .endpoint(c.getStringValue("endpoint"))
                    .region(c.getStringValue("region"))
                    .credentials(c.getStringValue("accesskey"), c.getStringValue("secretkey"))
                    .build();
        } catch (MissingConfigurationKeyException e) {
            e.printStackTrace();
        }

    }

    @Override
    public EngineItem set(EngineItem engineItem, InputStream input) {

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(engineItem.getPath())
                    .stream(input, input.available(), -1)
                    .build());

            return engineItem;
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public InputStream get(EngineItem engineItem) {

        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(engineItem.getPath()).build());
        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ArrayList<EngineItem> list(EngineItem engineItem) {

        ArrayList<EngineItem> result = new ArrayList<>();

        Iterable<Result<Item>> bucketList = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(engineItem.getPath())
                        .recursive(false)
                        .build()
        );

        for (Result<Item> resultItem : bucketList) {
            Item item = null;
            try {
                item = resultItem.get();

                EngineItemAttribute attribute = new EngineItemAttribute();
                attribute.isFile = !item.isDir();
                attribute.isDirectory = item.isDir();
                attribute.size = item.size();
                attribute.lastModified = item.lastModified().toLocalDateTime();

                EngineItem ei = new EngineItem(item.objectName(), attribute);
                result.add(ei);
            } catch (ErrorResponseException e) {
                e.printStackTrace();
            } catch (InsufficientDataException e) {
                e.printStackTrace();
            } catch (InternalException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidResponseException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (ServerException e) {
                e.printStackTrace();
            } catch (XmlParserException e) {
                e.printStackTrace();
            }

        }


        return result;
    }

    @Override
    public boolean exists(EngineItem engineItem) {

//        try {

        Iterable<Result<Item>> bucketList = minioClient.listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(bucketName)
                        .prefix(engineItem.getPath())
                        .recursive(false)
                        .maxKeys(1)
                        .build()
        );

        for (Result<Item> it : bucketList) {
            return true;
        }


        return false;
    }

    @Override
    public void delete(EngineItem engineItem) {
        try {

            Iterable<Result<Item>> bucketList = minioClient.listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket(bucketName)
                            .prefix(engineItem.getPath())
                            .recursive(true)
                            .build()
            );
            for (Result<Item> it : bucketList) {
                minioClient.removeObject(
                        RemoveObjectArgs
                                .builder()
                                .bucket(bucketName)
                                .object(it.get().objectName())
                                .build()
                );
            }

        } catch (ErrorResponseException e) {
            e.printStackTrace();
        } catch (InsufficientDataException e) {
            e.printStackTrace();
        } catch (InternalException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidResponseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (XmlParserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void move(EngineItem from, EngineItem to) {
        InputStream is = get(from);
        if (is != null) {
            set(to, is);
            delete(from);
        }
    }

    @Override
    public void mkdir(EngineItem item) throws IOException {
        if (exists(item)) {
            return;
        }
        EngineItemAttribute eia = new EngineItemAttribute();
        eia.isDirectory = true;

        EngineItem it = new EngineItem(item.getPath() + "/.dirFile", eia);

        String msg = "dummy file for mkdir";
        InputStream input = new ByteArrayInputStream(msg.getBytes());
        set(it, input);
    }


}
