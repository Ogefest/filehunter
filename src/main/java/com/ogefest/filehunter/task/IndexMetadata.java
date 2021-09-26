package com.ogefest.filehunter.task;

import com.ogefest.filehunter.*;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import java.io.*;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class IndexMetadata extends Task {

    private DirectoryIndex directoryIndex;
    private IndexWrite indexStorage;
    private IndexRead indexRead;
    private ArrayList<String> contentExtensions;

    private static final Logger LOG = Logger.getLogger(IndexMetadata.class);

    public IndexMetadata(DirectoryIndex directoryIndex) {
        this.directoryIndex = directoryIndex;

        contentExtensions = new ArrayList<>(Arrays.asList("pdf","doc","docx", "xls", "xlsx", "odt"));


//        this.indexRead = indexRead;
//        this.indexStorage = indexStorage;
    }

    @Override
    public void run() {

        if (!directoryIndex.isExtractMetadata()) {
            return;
        }
        indexStorage = getIndexWrite();
        indexRead = getIndexRead();

//        this.indexStorage = getApp().getIndexForWrite();
//        this.indexRead = getApp().getIndexForRead();

        if (!indexStorage.isStorageReady() || !indexRead.isStorageReady()) {
            LOG.info("Storage not ready");
            return;
        }

        ArrayList<FileInfo> fileList = indexRead.getAllForIndex(directoryIndex.getName());
        int counter = 0;
        for (FileInfo f : fileList) {

            if (!contentExtensions.contains(f.getExt())) {
                continue;
            }
            if (f.getSize() == 0) {
                continue;
            }


            if (counter > 100) {
                break;
            }

            if (f.getType() == FileType.FILE && f.getLastModified().isAfter(f.getLastMetaIndexed())) {
                updateMeta(f);

                counter++;
            }
        }

    }

    private void updateMeta(FileInfo fileInfo) {
        try {

            String content = plainContent(fileInfo.getPath());
            fileInfo.setContent(content.trim());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TikaException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        fileInfo.setLastMetaIndexed(LocalDateTime.now());
        try {
            indexStorage.addDocument(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String plainContent(String filename) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(100000);

        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try (InputStream stream = new FileInputStream(filename)) {
            parser.parse(stream, handler, metadata);

//            for (String name : metadata.names()) {
//                System.out.println(name + " " + metadata.get(name));
//            }

            return handler.toString();
        }
    }
}
