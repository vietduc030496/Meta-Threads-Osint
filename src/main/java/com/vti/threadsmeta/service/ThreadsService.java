package com.vti.threadsmeta.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vti.threadsmeta.dto.response.ThreadsItemResponse;
import com.vti.threadsmeta.repository.ThreadsAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadsService {

    @Autowired
    private ThreadsAPI threadsAPI;

    public ThreadsItemResponse getTheads(String threadsUrl) throws IOException {

        long threadsId = threadsAPI.getThreadsId(threadsUrl);
        String responseBody = threadsAPI.getThreads(threadsId);

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(responseBody, JsonObject.class);
        JsonObject data = jsonObject.getAsJsonObject("data").getAsJsonObject("data");
        JsonArray threadItems = data.getAsJsonObject("containing_thread").getAsJsonArray("thread_items");
        JsonObject post = threadItems.get(0).getAsJsonObject().getAsJsonObject("post");

        ThreadsItemResponse response = new ThreadsItemResponse();
        response.setId(post.getAsJsonPrimitive("pk").getAsString());
        response.setCaption(post.getAsJsonObject("caption").getAsJsonPrimitive("text").getAsString());
        response.setLikeCount(post.getAsJsonPrimitive("like_count").getAsLong());
        response.setReplyCount(post.getAsJsonObject("text_post_app_info").getAsJsonPrimitive("direct_reply_count").getAsLong());

        JsonElement carouselMediaObject = post.get("carousel_media");
        List<String> images = new ArrayList<>();
        if (!"null".equals(carouselMediaObject.toString())) {
            JsonArray carouselMedia = carouselMediaObject.getAsJsonArray();
            for (int i = 0; i < carouselMedia.size(); ++i) {
                JsonObject imageVersions2 = carouselMedia.get(i).getAsJsonObject().getAsJsonObject("image_versions2");
                JsonObject candidate = imageVersions2.getAsJsonArray("candidates").get(0).getAsJsonObject();
                String image = candidate.getAsJsonPrimitive("url").getAsString();

                images.add(image);
            }
        }
        response.setImages(images);

        JsonArray videoVersions = post.getAsJsonArray("video_versions");
        List<String> videos = new ArrayList<>();
        if (!videoVersions.isEmpty()) {
            String video = videoVersions.get(0).getAsJsonObject().getAsJsonPrimitive("url").getAsString();
            videos.add(video);
        }
        response.setVideos(videos);

        return response;
    }
}
