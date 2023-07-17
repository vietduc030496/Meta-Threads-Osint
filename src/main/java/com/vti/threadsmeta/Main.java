package com.vti.threadsmeta;

public class Main {
    public static void main(String[] args) {
        String url = "CuXFPIeLLod";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        long threadsId = 0;
        for (int i = 0; i < url.length(); ++i) {
            threadsId = (threadsId * 64) + alphabet.indexOf(url.charAt(i));
            System.out.println(threadsId);
        }
        System.out.println(threadsId);
    }
}
