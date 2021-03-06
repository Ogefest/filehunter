package com.ogefest.filehunter.task;

import com.ogefest.filehunter.BackendEngineFactory;
import com.ogefest.filehunter.Configuration;
import com.ogefest.filehunter.FileInfo;
import com.ogefest.filehunter.FileType;
import com.ogefest.filehunter.index.DirectoryIndex;
import com.ogefest.filehunter.storage.FileSystemDatabase;
import com.ogefest.filehunter.storage.LuceneSearch;
import com.ogefest.unifiedcloudfilesystem.EngineConfiguration;
import com.ogefest.unifiedcloudfilesystem.FileObject;
import com.ogefest.unifiedcloudfilesystem.UnifiedCloudFileSystem;
import io.quarkus.tika.TikaParseException;
import io.quarkus.tika.TikaParser;
import org.apache.log4j.Level;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.AutoDetectParser;
import org.jboss.logging.Logger;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

public class IndexMetadata extends Task {

    private static final Logger LOG = Logger.getLogger(IndexMetadata.class);
    private DirectoryIndex directoryIndex;
    private LuceneSearch luceneSearch;
    private ArrayList<String> contentExtensions;
    private TikaParser tikaParser;
    private FileSystemDatabase db;
    private Configuration conf;
    private UnifiedCloudFileSystem ucfs;

    public IndexMetadata(DirectoryIndex directoryIndex, Configuration conf) {
        this.directoryIndex = directoryIndex;
        this.conf = conf;

        contentExtensions = new ArrayList<>(Arrays.asList("pdf", "doc", "docx", "xls", "xlsx", "odt", "rtf", "txt", "csv"));

        java.util.logging.Logger
                .getLogger("org.apache.pdfbox").setLevel(java.util.logging.Level.OFF);


        String[] loggers = { "org.apache.pdfbox.util.PDFStreamEngine",
                "org.apache.pdfbox.pdmodel.font.PDSimpleFont",
                "org.apache.pdfbox.pdmodel.font.PDFont",
                "org.apache.pdfbox.pdmodel.font.FontManager",
                "org.apache.pdfbox.pdfparser.PDFObjectStreamParser" };
        for (String logger : loggers) {
            org.apache.log4j.Logger logpdfengine = org.apache.log4j.Logger
                    .getLogger(logger);
            logpdfengine.setLevel(Level.FATAL);
        }
    }

    @Override
    public void run() {

        this.db = getDatabase();

        EngineConfiguration ec = new EngineConfiguration(directoryIndex.getConfiguration());
        ucfs = new UnifiedCloudFileSystem();
        ucfs.registerEngine(directoryIndex.getName(), BackendEngineFactory.get(directoryIndex.getType(), ec));

        if (!directoryIndex.isExtractMetadata()) {
            return;
        }

        db.openReindexingSession(directoryIndex.getReindexSessionId(), directoryIndex);

        tikaParser = new TikaParser(new AutoDetectParser(), true);

        LuceneSearch search = new LuceneSearch(conf);

        String q = "tometareindex:t AND type:f";
        ArrayList<FileInfo> docsToReindex = search.queryByRawQuery(q, Integer.MAX_VALUE);
        int docsProcessed = 0;
        for (FileInfo fi : docsToReindex) {
            docsProcessed++;
            if (docsProcessed % 1000 == 0) {
                LOG.info("Meta reindex progress " + docsProcessed + "/" + docsToReindex.size());
            }

            String content = getContent(fi);

            fi.getFileAttributes().setContent(content);
            fi.getFileAttributes().setLastMetaIndexed(LocalDateTime.now());
            db.add(fi);

        }
        db.closeReindexingSession(directoryIndex.getReindexSessionId(), directoryIndex);
    }

    private String getContent(FileInfo fi) {
        if (!contentExtensions.contains(fi.getExt())) {
            return "";
        }
        if (fi.getSize() == 0) {
            return "";
        }
        if (fi.getSize() > 1024 * 1024) {
            return "";
        }
        if (fi.getFileAttributes().getType() != FileType.FILE) {
            return "";
        }

        FileObject fo = ucfs.getByPath(fi.getIndexName(), fi.getPath());
        try {
            return plainContent(ucfs.read(fo));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String plainContent(InputStream stream) throws IOException, TikaException, SAXException, TikaParseException {
        return tikaParser.parse(stream).getText();
    }
}
