package com.vti.threadsmeta.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MediaResponse {

    private List<String> image;
    private String video;
    private String name;
}
