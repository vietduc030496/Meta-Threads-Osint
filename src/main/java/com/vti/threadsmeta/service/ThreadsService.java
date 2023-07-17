package com.vti.threadsmeta.service;

import com.vti.threadsmeta.repository.ThreadsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ThreadsService {

    @Autowired
    private ThreadsAPI threadsAPI;

    public String getTheads(String threadsUrl) throws IOException {

        long threadsId = threadsAPI.getThreadsId(threadsUrl);
        String responseBody = threadsAPI.getThreads(threadsId);

        return "";
    }
}
