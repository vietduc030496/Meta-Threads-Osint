package com.vti.threadsmeta.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


/**
 *Represents an account on threads
 */


@Getter
@Setter
@AllArgsConstructor
public class ThreadsUser {

    private boolean isPrivate;
    private String profilePicUrl;
    private String username;
    private boolean isVerified;
    private String biography;
    private int followerCount;
    private String pk;
    private String fullName;
    private long id;

}
