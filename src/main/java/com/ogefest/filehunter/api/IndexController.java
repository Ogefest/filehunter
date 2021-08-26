package com.ogefest.filehunter.api;

import com.ogefest.filehunter.App;
import com.ogefest.filehunter.Directory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class IndexController {

    @Autowired
    private App app;

    @GetMapping("/index")
    public List<Directory> getAllIndexes() {
        return app.getConfiguration().getDirectories();
    }

    @PostMapping("/reindex")
    public void reindexAllDirectories() {
        app.reindex();
    }

}
