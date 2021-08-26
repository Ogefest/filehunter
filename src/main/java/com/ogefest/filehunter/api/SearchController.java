package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.util.List;

@RestController
public class SearchController {

    @Autowired
    private App app;

    @GetMapping("/search")
    public List<SearchResult> getSearchResult(@RequestParam("q") String query) {
        return app.search(query);
    }

    @GetMapping("/download/{uuid}")
    public ResponseEntity<FileSystemResource> getFile(@PathVariable("uuid") String uuid) {

        SearchResult res = app.getByUuid(uuid);
        if (res == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "file not found"
            );
        }

        File file = new File(res.getPath());
        long fileLength = file.length(); // this is ok, but see note below

        HttpHeaders respHeaders = new HttpHeaders();
        respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        respHeaders.setContentLength(fileLength);
        respHeaders.setContentDispositionFormData("attachment", "fileNameIwant.pdf");

        return new ResponseEntity<FileSystemResource>(
                new FileSystemResource(file), respHeaders, HttpStatus.OK
        );
    }

}
