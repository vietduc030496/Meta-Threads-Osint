package com.vti.threadsmeta.repository;

import com.vti.threadsmeta.dto.response.InstagramHeaderResponse;
import com.vti.threadsmeta.util.GraphConstants;
import com.vti.threadsmeta.util.HttpUtils;
import com.vti.threadsmeta.util.PatternUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ThreadsAPI {

    private Headers.Builder headerBuilder;
    private FormBody.Builder formBuilder;
    private InstagramHeaderResponse instagramHeader;

    public ThreadsAPI() {
        headerBuilder = new Headers.Builder().add("authority", "www.threads.net")
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
                .add("x-fb-lsd", "lDpRQj_6Y6EIsxPzJlbJB1");

        formBuilder = new FormBody.Builder().add("av", "0")
                .add("__user", "0")
                .add("__a", "1")
                .add("__req", "1")
                .add("__hs", "19544.HYP:barcelona_web_pkg.2.1..0.0")
                .add("dpr", "1")
                .add("__ccg", "EXCELLENT")
                .add("__rev", "1007795914")
                .add("__s", "c1fpxh:oh98tm:os2fqi")
                .add("__hsi", "7252655495199472548")
                .add("__dyn", "7xeUmwlEnwn8K2WnFw9-2i5U4e0yoW3q32360CEbo1nEhw2nVE4W0om78b87C0yE465o-cw5Mx62G3i0Bo7O2l0Fwqo31wnEfovwRwlE-U2zxe2Gew9O22362W2K0zK5o4q0GpovU1aUbodEGdwtU2ewbS1LwTwNwLw8O1pwr82gxC")
                .add("__csr", "j8kjt5p9e00hB4Eqw-w0Xiwrk0xE9Eixza2svazUndhEpko9xy7Ej7Saxl2U5-8m8yA4zCwxxWegQz5162a5x02UxW1g2Ex3MwM_3M25wlQ13gN0el4m2H3r16089wxwnq0w8gqd12")
                .add("__comet_req", "29")
//                .add("lsd", "lDpRQj_6Y6EIsxPzJlbJB1")
                .add("jazoest", "21997")
                .add("__spin_r", "1007795914")
                .add("__spin_b", "trunk")
                .add("__spin_t", "1688640447")
                .add("__jssesw", "2")
                .add("fb_api_caller_class", "RelayModern")
                .add("fb_api_req_friendly_name", "BarcelonaProfileRootQuery")
//                .add("variables", String.format(GraphConstants.VARIABLE_USER_ID, basicInfo.getUserId()))
                .add("server_timestamps", "true");
//                .add("doc_id", "23996318473300828");

        instagramHeader = getBasicInfo("");
    }

    public InstagramHeaderResponse getBasicInfo(String username) {
        try {
            if (instagramHeader != null && username.isEmpty()) {
                return instagramHeader;
            }

            String response = HttpUtils.doGet(String.format(GraphConstants.BASE_URL_WEB, username), headerBuilder.build());
            // remove ALL whitespaces from responseBody
            response = response.replaceAll("\\s", "");
            // remove all newlines from responseBody
            response = response.replaceAll("\\n", "");

            String regexUserId = "\"props\":\\{\"user_id\":\"(\\d+)\"\\},";
            String userId = PatternUtils.matcherPattern(response, regexUserId);

            String regexIgAppId = "\"customHeaders\":\\{\"X-IG-App-ID\":\"(\\d+)\"\\},";
            String igAppId = PatternUtils.matcherPattern(response, regexIgAppId);

            String regexFbToken = "\\{\"token\"\\s*:\\s*\"([^\"]+)\"\\}";
            String fbToken = PatternUtils.matcherPattern(response, regexFbToken);

            instagramHeader = new InstagramHeaderResponse(userId, igAppId, fbToken);
            return instagramHeader;
        } catch (IOException e) {
            return null;
        }
    }

    public String getByUsername(String username) throws IOException {
        instagramHeader = getBasicInfo(username);

        Headers header = headerBuilder.add("x-ig-app-id", instagramHeader.getIgAppId()).build();
        FormBody body = formBuilder.add("variables", String.format(GraphConstants.VARIABLE_USER_ID, instagramHeader.getUserId()))
                .add("doc_id", GraphConstants.DOC_ID_INFO)
                .build();

        return HttpUtils.doPost(GraphConstants.BASE_URL_GRAPH, header, body);
    }


    public String getMedia(String username) throws IOException {
        instagramHeader = getBasicInfo(username);
        Headers header = headerBuilder.add("x-ig-app-id", instagramHeader.getIgAppId()).build();
        FormBody body = formBuilder.add("variables", String.format(GraphConstants.VARIABLE_USER_ID, instagramHeader.getUserId()))
                .add("doc_id", GraphConstants.DOC_ID_MEDIA)
                .build();

        return HttpUtils.doPost(GraphConstants.BASE_URL_GRAPH, header, body);
    }

    public String getThreads(long threadsId) throws IOException {
        Headers header = headerBuilder.add("x-ig-app-id", instagramHeader.getIgAppId()).build();
        FormBody body = formBuilder.add("variables", String.format(GraphConstants.VARIABLE_POST_ID, threadsId))
                .add("doc_id", GraphConstants.DOC_ID_MEDIA)
                .add("lsd", instagramHeader.getFbLsd())
                .build();

        return HttpUtils.doPost(GraphConstants.BASE_URL_GRAPH, header, body);
    }

    public long getThreadsId(String threadsUrl) {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
        long threadsId = 0;
        for (int i = 0; i < threadsUrl.length(); ++i) {
            threadsId = (threadsId * 64) + alphabet.indexOf(threadsUrl.charAt(i));
        }
        return threadsId;
    }
}
