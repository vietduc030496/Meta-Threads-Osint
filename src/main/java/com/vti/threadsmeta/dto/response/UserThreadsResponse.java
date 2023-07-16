package com.vti.threadsmeta.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserThreadsResponse {

    private boolean isPrivate;
    private String username;
    private String profilePicture;
    private int followerCount;
    private boolean isVerified;
    private String id;
    private String fullName;

}
