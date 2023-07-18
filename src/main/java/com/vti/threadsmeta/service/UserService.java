package com.vti.threadsmeta.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vti.threadsmeta.dto.response.ThreadsMedia;
import com.vti.threadsmeta.dto.response.UserThreadsResponse;
import com.vti.threadsmeta.exception.custom.ThreadsUserNotFound;
import com.vti.threadsmeta.repository.ThreadsAPI;
import com.vti.threadsmeta.util.I18n;
import com.vti.threadsmeta.util.constants.MessageConstants;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ThreadsAPI threadsAPI;

    public UserThreadsResponse getByUsername(String username) throws IOException {
        String responseBody = threadsAPI.getByUsername(username);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonElement data = jsonObject.get("data");

        if ("null".equals(data.toString())) {
            throw new ThreadsUserNotFound(I18n.get(MessageConstants.MSG_001), username);
        }

        JsonObject userData = data.getAsJsonObject().getAsJsonObject("userData").getAsJsonObject("user");

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

    public String downloadImage(HttpServletResponse httpResponse, String username) throws IOException {
        String responseBody = threadsAPI.getAllMedia(username);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonElement data = jsonObject.get("data");

        if ("null".equals(data.toString())) {
            throw new ThreadsUserNotFound(I18n.get(MessageConstants.MSG_001), username);
        }

        JsonObject mediaData = data.getAsJsonObject().getAsJsonObject("mediaData");
        JsonArray threadsData = mediaData.getAsJsonArray("threads");

        ThreadsMedia media = new ThreadsMedia();
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

        return downloadZip(httpResponse, username, media);
    }

    public String downloadZip(HttpServletResponse httpResponse, String username, ThreadsMedia media) {
        try {
            // Create folder
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
            String currentDate = format.format(new Date());
            String folderDownload = "media/download/" + currentDate;
            String folderZip = "media/zip/" + currentDate;

            File folder = new File(folderDownload);
            folder.mkdirs();
            File folder2 = new File(folderZip);
            folder2.mkdirs();

            // Download image and video in from https://www.threads.net/
            for (String image : media.getImage()) {
                downloadMedia(image, folderDownload + "/" + UUID.randomUUID() + ".jpg");
            }
            downloadMedia(media.getVideo(), folderDownload + "/" + media.getName() + ".mp4");

            String zipFileName = username + ".zip";
            FileOutputStream fos = new FileOutputStream(folderZip + "/" + zipFileName);
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            File fileToZip = new File(folderDownload);
            zipFolder(fileToZip, fileToZip.getName(), zipOut);
            zipOut.close();
            fos.close();

            httpResponse.setContentType("application/zip");
            httpResponse.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");

            // Send zip file to client
            Path path = Paths.get(folderZip, zipFileName);
            try (InputStream inputStream = Files.newInputStream(path);
                 OutputStream outputStream = httpResponse.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // delete folder media and zip after download success
            FileUtils.deleteDirectory(new File(folderDownload));
            FileUtils.deleteDirectory(new File(folderZip));

            return "Download success.";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Download fail.";
    }

    private void downloadMedia(String url, String path) {
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

    private void zipFolder(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
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
        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
    }

}
