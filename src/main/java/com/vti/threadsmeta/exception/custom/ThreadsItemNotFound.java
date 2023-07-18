package com.vti.threadsmeta.exception.custom;

import lombok.Getter;

@Getter
public class ThreadsItemNotFound extends RuntimeException {

    private String item;

    public ThreadsItemNotFound(String message, String item) {
        super(message);
        this.item = item;
    }
}
