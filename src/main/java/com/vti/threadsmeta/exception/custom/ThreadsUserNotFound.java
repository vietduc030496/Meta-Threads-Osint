package com.vti.threadsmeta.exception.custom;

import lombok.Getter;

@Getter
public class ThreadsUserNotFound extends RuntimeException {

    private String username;
    public ThreadsUserNotFound(String message, String username) {
        super(message);
        this.username = username;
    }
}
