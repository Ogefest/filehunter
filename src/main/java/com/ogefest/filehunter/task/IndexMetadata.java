package com.ogefest.filehunter.task;

import com.ogefest.filehunter.*;
import com.ogefest.filehunter.search.IndexRead;
import com.ogefest.filehunter.search.IndexWrite;
import io.quarkus.tika.TikaParseException;
import io.quarkus.tika.TikaParser;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class IndexMetadata extends Task {

    private DirectoryIndex directoryIndex;
    private IndexWrite indexStorage;
    private IndexRead indexRead;
    private ArrayList<String> contentExtensions;

    private TikaParser tikaParser;

    private static final Logger LOG = Logger.getLogger(IndexMetadata.class);

    public IndexMetadata(DirectoryIndex directoryIndex) {
        this.directoryIndex = directoryIndex;

        contentExtensions = new ArrayList<>(Arrays.asList("pdf","doc","docx", "xls", "xlsx", "odt", "rtf", "txt", "csv"));

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

        tikaParser = new TikaParser(new AutoDetectParser(), true);

//        this.indexStorage = getApp().getIndexForWrite();
//        this.indexRead = getApp().getIndexForRead();

        if (!indexStorage.isStorageReady() || !indexRead.isStorageReady()) {
            LOG.debug("Storage not ready");
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


            if (counter > 1000) {
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

        } catch (Exception e) {
            LOG.warn("Unable to parse " + fileInfo.getPath());
        }

        fileInfo.setLastMetaIndexed(LocalDateTime.now());
        try {
            indexStorage.addDocument(fileInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String plainContent(String filename) throws IOException, TikaException, SAXException, TikaParseException {

        try  {
            InputStream stream = new FileInputStream(filename);
            return tikaParser.parse(stream).getText();
        } catch (Exception e) {
            LOG.warn("Unable to parse " + filename);
        }
        return "";

    }
}
