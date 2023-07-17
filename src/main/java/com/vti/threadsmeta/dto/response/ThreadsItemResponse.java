package com.vti.threadsmeta.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ThreadsItemResponse {

    private String id;

    private String caption;

    private List<String> images;

    private List<String> videos;

    @JsonProperty("like_count")
    private long likeCount;

    @JsonProperty("reply_count")
    private long replyCount;
}
