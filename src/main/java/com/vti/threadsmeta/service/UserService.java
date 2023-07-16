package com.vti.threadsmeta.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vti.threadsmeta.dto.response.CustomHeaderResponse;
import com.vti.threadsmeta.dto.response.MediaResponse;
import com.vti.threadsmeta.dto.response.UserThreadsResponse;
import com.vti.threadsmeta.util.GraphConstants;
import com.vti.threadsmeta.util.HttpUtils;
import com.vti.threadsmeta.util.PatternUtils;
import jakarta.servlet.http.HttpServletResponse;
import okhttp3.*;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class UserService {

    private Headers headers;

    public UserService() {
        headers = new Headers.Builder()
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

    private static void zipFolder(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFolder(childFile, fileName + "/" + childFile.getName(), zipOut);
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public UserThreadsResponse getByUsername(String username) throws IOException {
        CustomHeaderResponse basicInfo = getBasicInfo(username);
        RequestBody body = new FormBody.Builder()
                .add("av", "0")
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
                .add("lsd", "lDpRQj_6Y6EIsxPzJlbJB1")
                .add("jazoest", "21997")
                .add("__spin_r", "1007795914")
                .add("__spin_b", "trunk")
                .add("__spin_t", "1688640447")
                .add("__jssesw", "2")
                .add("fb_api_caller_class", "RelayModern")
                .add("fb_api_req_friendly_name", "BarcelonaProfileRootQuery")
                .add("variables", String.format(GraphConstants.VARIABLE_USERNAME, basicInfo.getUserId()))
                .add("server_timestamps", "true")
                .add("doc_id", "23996318473300828")
                .build();


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GraphConstants.BASE_URL_GRAPH)
                .headers(headers)
                .addHeader("x-ig-app-id", basicInfo.getIgAppId())
//                .addHeader("x-fb-lsd", basicInfo.getXFbLsdToken())
                .post(body)
                .build();

        String responseBody = "";
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            responseBody = response.body().string();
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonObject userData = jsonObject.getAsJsonObject("data").getAsJsonObject("userData").getAsJsonObject("user");

        boolean isPrivate = userData.getAsJsonPrimitive("is_private").getAsBoolean();
        String profilePicUrl = userData.getAsJsonPrimitive("profile_pic_url").getAsString();
        boolean isVerified = userData.getAsJsonPrimitive("is_verified").getAsBoolean();
        int followerCount = userData.getAsJsonPrimitive("follower_count").getAsInt();
        String pk = userData.getAsJsonPrimitive("pk").getAsString();
        String fullName = userData.getAsJsonPrimitive("full_name").getAsString();

        return UserThreadsResponse.builder()
                .isPrivate(isPrivate)
                .profilePicture(profilePicUrl)
                .username(username)
                .isVerified(isVerified)
                .followerCount(followerCount)
                .id(pk)
                .fullName(fullName)
                .build();
    }

    public void downloadImage(HttpServletResponse httpResponse, String username) throws IOException {
        CustomHeaderResponse basicInfo = getBasicInfo(username);
        RequestBody body = new FormBody.Builder()
                .add("av", "0")
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
                .add("lsd", "lDpRQj_6Y6EIsxPzJlbJB1")
                .add("jazoest", "21997")
                .add("__spin_r", "1007795914")
                .add("__spin_b", "trunk")
                .add("__spin_t", "1688640447")
                .add("__jssesw", "3")
                .add("fb_api_caller_class", "RelayModern")
                .add("fb_api_req_friendly_name", "BarcelonaProfileThreadsTabQuery")
                .add("variables", String.format(GraphConstants.VARIABLE_USERNAME, basicInfo.getUserId()))
                .add("server_timestamps", "true")
                .add("doc_id", GraphConstants.DOC_ID_MEDIA)
                .build();


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(GraphConstants.BASE_URL_GRAPH)
                .headers(headers)
                .addHeader("x-ig-app-id", basicInfo.getIgAppId())
//                .addHeader("x-fb-lsd", basicInfo.getXFbLsdToken())
                .post(body)
                .build();

        String responseBody = "";
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response code: " + response);
            }
            responseBody = response.body().string();
        }

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonObject mediaData = jsonObject.getAsJsonObject("data").getAsJsonObject("mediaData");

        JsonArray threadsData = mediaData.getAsJsonArray("threads");

        MediaResponse media = new MediaResponse();
        List<String> imageUrls = new ArrayList<>();
        for (int i = 0; i < threadsData.size(); i++) {
            JsonElement jsonElement = threadsData.get(i);
            JsonArray threadItems = jsonElement.getAsJsonObject().getAsJsonArray("thread_items");
            for (int j = 0; j < threadItems.size(); j++) {
                JsonObject post = threadItems.get(0).getAsJsonObject().getAsJsonObject("post");

                JsonElement mediaCount = post.get("carousel_media_count");
                if ("null".equals(mediaCount.toString())) {
                    JsonArray candidates = post.getAsJsonObject("image_versions2").getAsJsonArray("candidates");
                    if (!candidates.isEmpty()) {
                        String imageUrl = candidates.get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString();
                        imageUrls.add(imageUrl);
                    }
                } else {
                    JsonArray carouselMedia = post.getAsJsonArray("carousel_media");
                    for (int k = 0; k < carouselMedia.size(); ++k) {
                        JsonObject imageVersions2 = carouselMedia.get(k).getAsJsonObject().getAsJsonObject("image_versions2");
                        String imageUrl = imageVersions2.getAsJsonArray("candidates").get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString();
                        imageUrls.add(imageUrl);
                    }
                }

                JsonArray videoVersions = post.getAsJsonArray("video_versions");
                if (!videoVersions.isEmpty()) {
                    String videoUrl = videoVersions.get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString();
                    media.setVideo(videoUrl);
                }

            }
        }
        media.setImage(imageUrls);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String currentDate = format.format(new Date());
        String folderDownload = "media/download/" + currentDate;
        String folderZip = "media/zip/" + currentDate;

        File folder = new File(folderDownload);
        folder.mkdirs();
        File folder2 = new File(folderZip);
        folder2.mkdirs();

        for (String image : media.getImage()) {
            download(image, folderDownload + "/" + UUID.randomUUID() + ".jpg");
        }

        download(media.getVideo(), folderDownload + "/" + media.getName() + ".mp4");
        FileOutputStream fos = new FileOutputStream(folderZip + "/downloaded.zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        File fileToZip = new File(folderDownload);
        zipFolder(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();

        String zipFileName = "downloaded.zip";
        httpResponse.setContentType("application/zip");
        httpResponse.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

        // Gửi tệp tin nén đến client
        Path path = Paths.get(folderZip + "/" + zipFileName);
        try (InputStream inputStream = Files.newInputStream(path);
             OutputStream outputStream = httpResponse.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        // delete folder media and zip
        FileUtils.deleteDirectory(new File(folderDownload));
        FileUtils.deleteDirectory(new File(folderZip));
    }

    private CustomHeaderResponse getBasicInfo(String username) throws IOException {
        String response = HttpUtils.doGet(String.format(GraphConstants.BASE_URL_WEB, username), headers);
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

        return new CustomHeaderResponse(userId, igAppId);
    }

    private void download(String url, String path) {
        if (url == null || url.isEmpty()) {
            return;
        }

        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // handle exception
        }
    }

}
