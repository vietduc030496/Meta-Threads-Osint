package com.vti.threadsmeta.controller;

import com.vti.threadsmeta.service.ThreadsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/threads")
public class ThreadsController {

    @Autowired
    private ThreadsService threadsService;

    @GetMapping("/{threads}")
    public ResponseEntity<Object> getThreads(@PathVariable("threads") String threadUrl) throws IOException {
        String theads = threadsService.getTheads(threadUrl);
        return null;
    }
}
