package com.vti.threadsmeta.util;

import com.vti.threadsmeta.dto.common.CustomHeader;
import okhttp3.*;

import java.io.IOException;

public class HttpUtils {

    private static Headers defaultHeaders;

    public static void init() {
        if (defaultHeaders != null) {
            return;
        }
        defaultHeaders = new Headers.Builder()
                .add("authority", "www.threads.net")
                .add("accept", "*/*")
                .add("accept-language", "vi-VN,vi;q=0.9,fr-FR;q=0.8,fr;q=0.7,en-US;q=0.6,en;q=0.5")
                .add("content-type", "application/x-www-form-urlencoded")
                .add("origin", "https://www.threads.net")
                .add("referer", "https://www.threads.net/@kopie3496")
                .add("sec-ch-prefers-color-scheme", "light")
                .add("sec-ch-ua", "\"Not.A/Brand\";v=\"8\", \"Chromium\";v=\"114\", \"Google Chrome\";v=\"114\"")
                .add("sec-ch-ua-full-version-list", "\"Not.A/Brand\";v=\"8.0.0.0\", \"Chromium\";v=\"114.0.5735.199\", \"Google Chrome\";v=\"114.0.5735.199\"")
                .add("sec-ch-ua-mobile", "?0")
                .add("sec-ch-ua-platform", "\"Windows\"")
                .add("sec-ch-ua-platform-version", "\"10.0.0\"")
                .add("sec-fetch-dest", "empty")
                .add("sec-fetch-mode", "cors")
                .add("sec-fetch-site", "same-origin")
                .add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .add("viewport-width", "1920")
                .add("x-asbd-id", "129477")
                .add("x-fb-friendly-name", "BarcelonaProfileRootQuery")
                .add("x-fb-lsd", "lDpRQj_6Y6EIsxPzJlbJB1") // 1jp4orW6PZ_H1Kmq6UZXO9
                .build();
    }

    public static String doGet(String url, Headers headers) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url)
                .headers(headers)
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            return response.body().string();
        }
    }

    public static String doPost(String url, Headers headers, RequestBody body) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url)
                .headers(headers)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            return response.body().string();
        }
    }
}
