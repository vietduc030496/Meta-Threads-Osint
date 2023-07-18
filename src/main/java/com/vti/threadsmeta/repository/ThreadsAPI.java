package com.vti.threadsmeta.repository;

import com.vti.threadsmeta.dto.response.InstagramHeaderResponse;
import com.vti.threadsmeta.util.constants.GraphConstants;
import com.vti.threadsmeta.util.HttpUtils;
import com.vti.threadsmeta.util.PatternUtils;
import okhttp3.FormBody;
import okhttp3.Headers;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ThreadsAPI {

    private Headers defaultHeader;
    private InstagramHeaderResponse instagramHeader;

    public ThreadsAPI() {
        defaultHeader = new Headers.Builder()
                .add("sec-fetch-dest", "empty")
                .add("sec-fetch-mode", "cors")
                .add("sec-fetch-site", "same-origin")
                .add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .add("viewport-width", "1920")
                .add("x-asbd-id", "129477")
                .add("x-csrftoken", "")
                .add("x-fb-friendly-name", "BarcelonaProfileRootQuery")
                .build();

        instagramHeader = getBasicInfo("");
    }

    public String getByUsername(String username) throws IOException {
        instagramHeader = getBasicInfo(username);

        Headers header = new Headers.Builder()
                .addAll(defaultHeader)
                .add("x-fb-lsd", instagramHeader.getFbLsd())
                .add("x-ig-app-id", instagramHeader.getIgAppId())
                .build();

        FormBody body = new FormBody.Builder()
                .add("lsd", instagramHeader.getFbLsd())
                .add("variables", String.format(GraphConstants.VARIABLE_USER_ID, instagramHeader.getUserId()))
                .add("doc_id", GraphConstants.DOC_ID_USER_INFO)
                .build();

        return HttpUtils.doPost(GraphConstants.BASE_URL_GRAPH, header, body);
    }

    public String getAllMedia(String username) throws IOException {
        instagramHeader = getBasicInfo(username);

        Headers header = new Headers.Builder()
                .addAll(defaultHeader)
                .add("x-fb-lsd", instagramHeader.getFbLsd())
                .add("x-ig-app-id", instagramHeader.getIgAppId())
                .build();

        FormBody body = new FormBody.Builder()
                .add("lsd", instagramHeader.getFbLsd())
                .add("variables", String.format(GraphConstants.VARIABLE_USER_ID, instagramHeader.getUserId()))
                .add("doc_id", GraphConstants.DOC_ID_USER_MEDIA)
                .build();

        return HttpUtils.doPost(GraphConstants.BASE_URL_GRAPH, header, body);
    }

    public String getThreadsById(long threadsId) throws IOException {

        Headers header = new Headers.Builder()
                .addAll(defaultHeader)
                .add("x-fb-lsd", instagramHeader.getFbLsd())
                .add("x-ig-app-id", instagramHeader.getIgAppId())
                .build();

        FormBody body = new FormBody.Builder()
                .add("lsd", instagramHeader.getFbLsd())
                .add("variables", String.format(GraphConstants.VARIABLE_POST_ID, threadsId))
                .add("doc_id", GraphConstants.DOC_ID_THREAD_MEDIA)
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

    public InstagramHeaderResponse getBasicInfo(String username) {
        try {
            if (instagramHeader != null && username.isEmpty()) {
                return instagramHeader;
            }

            String response = HttpUtils.doGet(String.format(GraphConstants.BASE_URL_WEB, username), defaultHeader);
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
            e.printStackTrace();
            return null;
        }
    }
}
